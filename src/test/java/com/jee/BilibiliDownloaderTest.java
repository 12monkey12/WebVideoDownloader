package com.jee;

import com.jee.download.Downloader;
import com.jee.download.DownloaderFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;

/**
 * @program: WebVideoDownloader
 * @description:
 * @author: animal
 * @create: 2022-11-27 00:04
 **/
@SpringBootTest
public class BilibiliDownloaderTest {

    @Autowired
    private DownloaderFactory downloaderFactory;

    /**
     * ffmpeg -i video.m4s -i audio.m4s -codec copy output.mp4
     * @throws Exception
     */
    @Test
    public void bilibiliTest() throws Exception {
        String url = "https://www.bilibili.com/video/BV1Ev4y1d7By/?spm_id_from=333.851.b_7265706f7274466972737432.6&vd_source=3c1de7750ea47eddcc8ad95c1f2a8ca9";
        String type = "bilibili";
        Downloader downloader = downloaderFactory.createDownloader(type);
//        Path path = downloader.download(url);
    }

}
