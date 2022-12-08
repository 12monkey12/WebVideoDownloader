package com.jee;

import com.jee.service.BilibiliDownloader;
import com.jee.service.DownloaderServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @program: WebVideoDownloader
 * @description:
 * @author: animal
 * @create: 2022-11-26 22:07
 **/
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        DownloaderServer downloaderServer = context.getBean(DownloaderServer.class);
        downloaderServer.start();

        SpringApplication.exit(context);
    }
}
