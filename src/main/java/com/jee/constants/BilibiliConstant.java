package com.jee.constants;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @program: WebVideoDownloader
 * @description:
 * @author: animal
 * @create: 2022-11-26 22:07
 **/
public class BilibiliConstant {

    /**
     * 视频信息
     */
    public static final String VIDEO_INFO_URL = "https://api.bilibili.com/x/web-interface/view?bvid=%s";
    /**
     * 下载信息
     */
    public static final String VIDEO_PLAYURL = "https://api.bilibili.com/x/player/playurl";
    /**
     * 验证码
     */
    public static final String CAPTCHA_URL = "http://passport.bilibili.com/x/passport-login/captcha?source=main_web";
    /**
     * 获取公钥 & 盐
     */
    public static final String HASH_URL = "http://passport.bilibili.com/x/passport-login/web/key";
    /**
     * 登录
     */
    public static final String BILIBILI_LOGIN_URL = "http://passport.bilibili.com/x/passport-login/web/login";


    /**
     * BV号模式
     */
    public static final Pattern BV_PATTERN = Pattern.compile("BV([0-9A-Za-z]+)");


    /**
     * 登录信息 cookie.txt
     */
    public static List<Cookie> loginCookie = new ArrayList<>();

}
