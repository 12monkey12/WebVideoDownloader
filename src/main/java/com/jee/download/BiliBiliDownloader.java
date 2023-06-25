package com.jee.download;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jee.constants.BilibiliConstant;
import com.jee.constants.CommonConstant;
import com.jee.po.BiliBiliVideoInfo;
import com.jee.po.VideoInfo;
import com.jee.utils.HTTPUtils;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @program: WebVideoDownloader
 * @description:
 * @author: animal
 * @create: 2022-12-10 15:45
 **/
@Component
@Slf4j
public class BiliBiliDownloader implements Downloader {

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${download.video.storeDir}")
    private String downloadDir;

    private String cookie;

    @PostConstruct
    public void init() throws IOException {
        ClassPathResource resource = new ClassPathResource(CommonConstant.COOKIE_PATH);
        if (!resource.exists()) {
            return;
        }

        File file = resource.getFile();
        Path path = file.toPath();
        List<String> allLines = Files.readAllLines(path);
        String text = String.join("", allLines);
        this.cookie = text;
    }

    @Override
    public VideoInfo load(String url) throws Exception {
        BiliBiliVideoInfo videoInfo = new BiliBiliVideoInfo();
        videoInfo.setUrl(url);
        parseVideoUrl(videoInfo);
        return videoInfo;
    }

    @Override
    public void download(VideoInfo videoInfo) throws Exception {
        BiliBiliVideoInfo biliBiliVideoInfo = (BiliBiliVideoInfo) videoInfo;
        Path path = doDownload(biliBiliVideoInfo);
    }

    /**
     * 解析 videoUrl
     */
    private void parseVideoUrl(BiliBiliVideoInfo videoInfo) throws Exception{
        String videoUrl = videoInfo.getUrl();
        Matcher matcher = BilibiliConstant.BV_PATTERN.matcher(videoUrl);
        matcher.find();
        String bvid = matcher.group();
        videoInfo.setBvid(bvid);

        String videoInfoReqUrl = String.format(BilibiliConstant.VIDEO_INFO_URL, bvid);
        HttpGet httpGet = new HttpGet(videoInfoReqUrl);
        if (StringUtils.isNotBlank(cookie)) {
            List<Header> headers = new ArrayList<>();
            headers.add(new BasicHeader("cookie", cookie));
            httpGet.setHeaders(headers.toArray(new Header[0]));
        }
        String respJson = HTTPUtils.httpGetToString(httpGet);
        // 解析视频信息获取视频 cid
        JsonNode jsonNode = objectMapper.readTree(respJson);
        JsonNode dataJson = jsonNode.get("data");
        String title = dataJson.get("title").asText();
        String cid = dataJson.get("cid").asText();
        String avid = dataJson.get("aid").asText();
        videoInfo.setAvid(avid);
        videoInfo.setCid(cid);
        videoInfo.setTitle(title);

        // 获取下载连接
        Map<String, String> params = new HashMap<>();
        params.put("avid", avid);
        params.put("bvid", bvid);
        params.put("cid", cid);
        params.put("fnval", "16");
        params.put("qn", "116");

        URIBuilder uriBuilder = new URIBuilder(BilibiliConstant.VIDEO_PLAYURL);
        // 拼接请求参数
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            uriBuilder.setParameter(key, value);
        }
        String playerUrl = uriBuilder.build().toString();
        HttpGet playerHttpGet = new HttpGet(playerUrl);

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("origin", "https://www.bilibili.com"));
        headers.add(new BasicHeader("referer", videoUrl));
        if (StringUtils.isNotBlank(cookie)) {
            headers.add(new BasicHeader("cookie", cookie));
        }
        playerHttpGet.setHeaders(headers.toArray(new Header[0]));

        String downloadRespStr = HTTPUtils.httpGetToString(playerHttpGet);
        Map map = objectMapper.readValue(downloadRespStr, Map.class);
        Map dataMap = (Map) map.get("data");
        List<Map> supportFormats = (List<Map>) dataMap.get("support_formats");
        // 获取视频分辨率列表
        List<Pair<Integer, String>> resolutions = new ArrayList<>();
        for (Map supportFormat : supportFormats) {
            Integer quality = (Integer) supportFormat.get("quality");
            String displayDesc = (String) supportFormat.get("display_desc");
            Pair<Integer, String> pair = new Pair<>(quality, displayDesc);
            resolutions.add(pair);
        }
        videoInfo.setResolutions(resolutions);

        // 获取音频和视频下载信息
        Map dashMap = (Map) dataMap.get("dash");
        List<Map> videoList = (List<Map>) dashMap.get("video");
        Map<Integer, String> videoDownloadMap = videoList.stream()
                .filter(po -> Integer.valueOf(7).equals(po.get("codecid")))
                .collect(Collectors.groupingBy(po -> (Integer) po.get("id")))
                .entrySet().stream()
                .collect(Collectors.toMap(po -> po.getKey(), po -> (String) po.getValue().get(0).get("baseUrl"), (po1, po2) -> po1));
        videoInfo.setVideoDownloadMap(videoDownloadMap);

        List<Map> audioList = (List<Map>) dashMap.get("audio");
        Map<Integer, String> audioDownloadMap = audioList.stream()
                .collect(Collectors.groupingBy(po -> (Integer) po.get("id")))
                .entrySet().stream()
                .collect(Collectors.toMap(po -> po.getKey(), po -> (String) po.getValue().get(0).get("baseUrl"), (po1, po2) -> po1));
        videoInfo.setAudioDownloadMap(audioDownloadMap);
    }

    private Path doDownload(BiliBiliVideoInfo videoInfo) throws IOException {
        Map<Integer, String> audioDownloadMap = videoInfo.getAudioDownloadMap();
        Map<Integer, String> videoDownloadMap = videoInfo.getVideoDownloadMap();
        String videoUrl = videoInfo.getUrl();

        // 下载音频文件
        String audioDownloadUrl = audioDownloadMap.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<Integer, String> po) -> po.getKey()).reversed())
                .map(po -> po.getValue())
                .findFirst()
                .get();
        HttpGet audioHttpGet = new HttpGet(audioDownloadUrl);
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("origin", "https://www.bilibili.com"));
        headers.add(new BasicHeader("referer", videoUrl));
        if (StringUtils.isNotBlank(cookie)) {
            headers.add(new BasicHeader("cookie", cookie));
        }
        audioHttpGet.setHeaders(headers.toArray(new Header[0]));
        byte[] audioBuffer = HTTPUtils.httpGetToByteArray(audioHttpGet);
        Path audioPath = Paths.get(downloadDir, UUID.randomUUID().toString() + ".m4s");
        Files.deleteIfExists(audioPath);
        Files.copy(new ByteArrayInputStream(audioBuffer), audioPath);

        // 下载视频文件
        String videoDownloadUrl = videoDownloadMap.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<Integer, String> po) -> po.getKey()).reversed())
                .map(po -> po.getValue())
                .findFirst()
                .get();
        HttpGet videoHttpGet = new HttpGet(videoDownloadUrl);
        videoHttpGet.setHeaders(headers.toArray(new Header[0]));
        byte[] videoBuffer = HTTPUtils.httpGetToByteArray(videoHttpGet);
        Path videoPath = Paths.get(downloadDir, UUID.randomUUID().toString() + ".m4s");
        Files.deleteIfExists(videoPath);
        Files.copy(new ByteArrayInputStream(videoBuffer), videoPath);

        return mergeVideoAndAudio(videoPath, audioPath, videoInfo);
    }

    /**
     * 合并音视频
     * ffmpeg -i video.m4s -i audio.m4s -codec copy output.mp4
     * @param videoPath
     * @param audioPath
     * @return
     */
    private Path mergeVideoAndAudio(Path videoPath, Path audioPath, BiliBiliVideoInfo videoInfo) {
        List<String> command = new ArrayList<>();
        command.add("D:\\carry\\ffmpeg.exe");
        command.add("-i");
        command.add(videoPath.toString());
        command.add("-i");
        command.add(audioPath.toString());
        command.add("-codec");
        command.add("copy");
        String outputDir = downloadDir + File.separator + videoInfo.getTitle() + ".mp4";
        command.add(outputDir);
        log.info("ffmpeg cmd：{}", String.join(" ", command));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        // 设置工作目录
        String property = System.getProperty("user.dir");
        log.info("user.dir：{}",  property);
        String workDir = "D:\\carry";
        File file = Paths.get(workDir).toFile();
        processBuilder.directory(file);
        try {
            Process process = processBuilder.start();
            InputStream errorStream = process.getErrorStream();
            if (Objects.nonNull(errorStream)) {
                InputStreamReader errorStreamReader = new InputStreamReader(errorStream);
                char[] buffer = new char[1024];
                StringBuilder errorStringBuilder = new StringBuilder();
                int length = 0;
                while ((length = errorStreamReader.read(buffer)) != -1){
                    errorStringBuilder.append(buffer, 0, length);
                }
                log.error(errorStringBuilder.toString());
            }

            InputStream inputStream = process.getInputStream();
            if (Objects.nonNull(inputStream)) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                char[] buffer = new char[1024];
                StringBuilder inputStringBuilder = new StringBuilder();
                int length = 0;
                while ((length = inputStreamReader.read(buffer)) != -1){
                    inputStringBuilder.append(buffer, 0, length);
                }
                log.info(inputStringBuilder.toString());
            }

            process.waitFor();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Paths.get(outputDir);
    }

}
