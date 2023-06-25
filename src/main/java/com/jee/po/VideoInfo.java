package com.jee.po;

import javafx.util.Pair;
import lombok.Data;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * @program: WebVideoDownloader
 * @description:
 * @author: animal
 * @create: 2022-12-10 15:48
 **/
@Data
public class VideoInfo {

    /**
     * 视频链接
     */
    private String url;
    /**
     * 标题
     */
    private String title;
    /**
     * 作者
     */
    private String author;
    /**
     * 本地路径
     */
    private Path localPath;
    /**
     * 分辨率
     */
    private List<Pair<Integer, String>> resolutions;

}
