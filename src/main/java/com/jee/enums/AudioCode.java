package com.jee.enums;

import lombok.Getter;

/**
 * 视频伴音音质代码
 */
@Getter
public enum AudioCode {

    SIXTY_FOUR(30216, "64K"),
    ONE_HUNDRED_AND_THIRTY_TWO(30232, "132K"),
    ONE_HUNDRED_AND_NINETY_TWO(30280, "192K"),
    DOLBT(30250, "杜比全景声"),
    HI_RES(30251, "Hi-Res无损");



    private String desc;
    private int value;

    AudioCode(int value, String desc){
        this.value = value;
        this.desc = desc;
    }
}
