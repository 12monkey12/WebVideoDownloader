package com.jee.download;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @program: WebVideoDownloader
 * @description:
 * @author: animal
 * @create: 2022-12-10 16:29
 **/
@Component
public class DownloaderFactory implements ApplicationContextAware {

    private ApplicationContext context;

    public Downloader createDownloader(String type){
        if ("bilibili".equals(type)) {
            return context.getBean("biliBiliDownloader", Downloader.class);
        }

        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
