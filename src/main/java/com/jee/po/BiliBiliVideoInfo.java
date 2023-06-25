package com.jee.po;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @program: gradle-demo
 * @description:
 * @author: animal
 * @create: 2022-11-14 15:30
 **/
@Getter
@Setter
public class BiliBiliVideoInfo extends VideoInfo {

    /**
     * 稿件bvid
     */
    private String bvid;
    /**
     * 稿件avid
     */
    private String avid;
    /**
     * 视频 cid
     */
    private String cid;

    /**
     * 视频下载地址
     */
    private Map<Integer, String> videoDownloadMap;
    /**
     * 音频下载地址
     */
    private Map<Integer, String> audioDownloadMap;

}
