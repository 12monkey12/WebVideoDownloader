package com.jee.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * @program: WebVideoDownloader
 * @description:
 * @author: animal
 * @create: 2022-11-26 23:50
 **/
@Component
@Slf4j
public class DownloaderServer {

    /**
     * 关闭码
     */
    public static final String SHUTDOWN = "exit";

    @Autowired
    private BilibiliDownloader bilibiliDownloader;

    public void start(){
        Scanner in = new Scanner(System.in);
        while (in.hasNextLine()) {
            String msg = in.nextLine();
            if (SHUTDOWN.equals(msg)) {
                break;
            }

            try {
                bilibiliDownloader.download(msg);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }


    }

}
