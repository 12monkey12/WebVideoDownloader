package com.jee.download;

import com.jee.po.VideoInfo;


/**
 * 下载器
 */
public interface Downloader {

    VideoInfo load(String url) throws Exception;

    void download(VideoInfo videoInfo) throws Exception;
}
