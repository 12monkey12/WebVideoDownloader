package com.jee.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jee.constants.BilibiliConstants;
import com.jee.entity.BilibiliVideoInfo;
import com.jee.enums.VideoCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @program: gradle-demo
 * @description:
 * @author: animal
 * @create: 2022-11-14 10:57
 **/
@Slf4j
@Component
public class BilibiliDownloader {

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${download.video.storeDir}")
    private String downloadDir;
    @Value("${bilibili.cookie}")
    private String cookie;

    private BilibiliVideoInfo bilibiliVideoInfo;

    public BilibiliDownloader(){
        bilibiliVideoInfo = new BilibiliVideoInfo();
    }

    /**
     * 下载视频
     * @param url
     * @throws Exception
     */
    public void download(String url) throws Exception {
        // 登录 cookie
        if (StringUtils.isBlank(cookie)) {
            log.info("user is not login");
        }

        log.info("bilibili video url：{}", url);
        bilibiliVideoInfo.setOriginUrl(url);
        // 解析 url
        parseOriginalUrl();
        log.info("bilibiliVideoInfo：{}", bilibiliVideoInfo);

        // 下载视频
        String videoUrl = bilibiliVideoInfo.getVideoUrl();
        HttpGet httpGet = new HttpGet(videoUrl);
        // 填充标头
        String originUrl = bilibiliVideoInfo.getOriginUrl();
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("origin", "https://www.bilibili.com"));
        headers.add(new BasicHeader("referer", originUrl));
        // 填充 cookie
        if (StringUtils.isNotBlank(cookie)) {
            headers.add(new BasicHeader("cookie", cookie));
            httpGet.setHeaders(headers.toArray(new Header[0]));
        }
        httpGet.setHeaders(headers.toArray(new Header[0]));

        byte[] bytes = null;
        try (
                CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
                ) {
            HttpEntity entity = httpResponse.getEntity();
            bytes = EntityUtils.toByteArray(entity);
        }
        Path videoPath = Paths.get(downloadDir, UUID.randomUUID().toString() + ".m4s");
        Files.deleteIfExists(videoPath);
        Files.copy(new ByteArrayInputStream(bytes), videoPath);

        // 下载音频
        String audioUrl = bilibiliVideoInfo.getAudioUrl();
        HttpGet audioHttpGet = new HttpGet(audioUrl);
        // 填充标头
        List<Header> audioHeaders = new ArrayList<>();
        audioHeaders.add(new BasicHeader("origin", "https://www.bilibili.com"));
        audioHeaders.add(new BasicHeader("referer", originUrl));
        // 填充 cookie
        if (StringUtils.isNotBlank(cookie)) {
            audioHeaders.add(new BasicHeader("cookie", cookie));
        }
        audioHttpGet.setHeaders(audioHeaders.toArray(new Header[0]));

        byte[] audioBytes = null;
        try (
                CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse httpResponse = httpClient.execute(audioHttpGet);
        ) {
            HttpEntity entity = httpResponse.getEntity();
            audioBytes = EntityUtils.toByteArray(entity);
        }
        Path audioPath = Paths.get(downloadDir, UUID.randomUUID().toString() + ".m4s");
        Files.deleteIfExists(audioPath);
        Files.copy(new ByteArrayInputStream(audioBytes), audioPath);

        log.info("video path: {}, audio path: {}", videoPath.toString(), audioPath.toString());

        Path mergePath = mergeVideoAndAudio(videoPath, audioPath);
        log.info("download end,video path: {}", mergePath.toString());
    }

    /**
     * 合并音视频
     * ffmpeg -i video.m4s -i audio.m4s -codec copy output.mp4
     * @param videoPath
     * @param audioPath
     * @return
     */
    private Path mergeVideoAndAudio(Path videoPath, Path audioPath) {
        List<String> command = new ArrayList<>();
        command.add("D:\\carry\\ffmpeg.exe");
        command.add("-i");
        command.add(videoPath.toString());
        command.add("-i");
        command.add(audioPath.toString());
        command.add("-codec");
        command.add("copy");
        String outputDir = downloadDir + File.separator + bilibiliVideoInfo.getTitle() + ".mp4";
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

    private void parseOriginalUrl() throws Exception {
        String originUrl = bilibiliVideoInfo.getOriginUrl();
        Matcher matcher = BilibiliConstants.BV_PATTERN.matcher(originUrl);
        if (!matcher.find()) {
            throw new RuntimeException("url 中没有找到 bvid，请求 url：" + originUrl);
        }
        String bvid = matcher.group();
        bilibiliVideoInfo.setBvid(bvid);

        String videoInfoReqUrl = String.format(BilibiliConstants.VIDEO_INFO_URL, bvid);
        HttpGet httpGet = new HttpGet(videoInfoReqUrl);
        if (StringUtils.isNotBlank(cookie)) {
            List<Header> headers = new ArrayList<>();
            headers.add(new BasicHeader("cookie", cookie));
            httpGet.setHeaders(headers.toArray(new Header[0]));
        }
        String respJson = null;
        try (
                CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        ) {
            HttpEntity entity = httpResponse.getEntity();
            respJson = EntityUtils.toString(entity);
        }

        // 解析视频信息
        JsonNode videoInfo = objectMapper.readTree(respJson);
        JsonNode dataJson = videoInfo.get("data");
        String title = dataJson.get("title").asText();
        String cid = dataJson.get("cid").asText();
        String avid = dataJson.get("aid").asText();
        bilibiliVideoInfo.setAvid(avid);
        bilibiliVideoInfo.setCid(cid);
        bilibiliVideoInfo.setTitle(title);

        // 获取下载连接
        Map<String, String> params = new HashMap<>();
        params.put("avid", avid);
        params.put("bvid", bvid);
        params.put("cid", cid);
        params.put("fnval", "16");

        URIBuilder uriBuilder = new URIBuilder(BilibiliConstants.VIDEO_PLAYURL);
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
        headers.add(new BasicHeader("referer", originUrl));
        if (StringUtils.isNotBlank(cookie)) {
            headers.add(new BasicHeader("cookie", cookie));
        }
        playerHttpGet.setHeaders(headers.toArray(new Header[0]));

        String downloadRespStr = null;
        try (
                CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse httpResponse = httpClient.execute(playerHttpGet);
        ) {
            HttpEntity entity = httpResponse.getEntity();
            downloadRespStr = EntityUtils.toString(entity);
        }
        JsonNode downloadInfoJson = objectMapper.readTree(downloadRespStr);
        JsonNode downloadDataJson = downloadInfoJson.get("data");
        JsonNode dashJson = downloadDataJson.get("dash");
        JsonNode videoJson = dashJson.get("video");
        JsonNode audioJson = dashJson.get("audio");

        List<Map<String, Object>> videoList = new ArrayList<>();
        for (JsonNode item : videoJson) {
            Map<String, Object> map = new HashMap<>();
            int id = item.get("id").asInt();
            String baseUrl = item.get("baseUrl").asText();
            int codecid = item.get("codecid").asInt();
            map.put("id", id);
            map.put("baseUrl", baseUrl);
            map.put("codecid", codecid);
            videoList.add(map);
        }
        Map<String, Object> videoMap = videoList.stream()
                .filter(po -> Integer.valueOf(VideoCode.AVC.getValue()).equals(po.get("codecid")))
                .sorted(Comparator.comparingInt((Map<String, Object> po) -> (Integer) po.get("id")).reversed())
                .findFirst()
                .get();
        String videoUrl = (String) videoMap.get("baseUrl");
        bilibiliVideoInfo.setVideoUrl(videoUrl);

        List<Map<String, Object>> audioList = new ArrayList<>();
        for (JsonNode item : audioJson) {
            Map<String, Object> map = new HashMap<>();
            int id = item.get("id").asInt();
            String baseUrl = item.get("baseUrl").asText();
            int codecid = item.get("codecid").asInt();
            map.put("id", id);
            map.put("baseUrl", baseUrl);
            map.put("codecid", codecid);
            audioList.add(map);
        }
        Map<String, Object> audioMap = audioList.stream()
                .sorted(Comparator.comparing((Map<String, Object> po) -> (Integer) po.get("id")).reversed())
                .findFirst()
                .get();
        String audioUrl = (String) audioMap.get("baseUrl");
        bilibiliVideoInfo.setAudioUrl(audioUrl);
    }

}
