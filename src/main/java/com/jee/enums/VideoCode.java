package com.jee.enums;

import lombok.Getter;

/**
 * 视频编码代码
 */
@Getter
public enum VideoCode {

    AVC(7, "AVC编码"),
    HEVC(12, "HEVC编码"),
    AV1(13, "编码");

    private String desc;
    private int value;

    VideoCode(int value, String desc){
        this.value = value;
        this.desc = desc;
    }
}
