package com.jee.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.Header;

import java.util.List;

/**
 * @program: WebVideoDownloader
 * @description:
 * @author: animal
 * @create: 2022-11-27 11:26
 **/
@Getter
@Setter
public class UserInfo {

    /**
     * 验证方式
     * geetest：极验
     */
    private String captchaType;
    /**
     * 登录 API token
     */
    private String captchaToken;
    /**
     * 极验id
     */
    private String captchaGT;
    /**
     * 极验KEY
     */
    private String captchaChallenge;

    /**
     * 密码盐值
     * 有效时间为 20s
     * 恒为 16 字符
     * 需要拼接在明文密码之前
     */
    private String hash;
    /**
     * rsa 公钥
     */
    private String rsaPubKey;


    /**
     * 用户名
     */
    private String username;
    /**
     * 明文密码
     */
    private String password;
    /**
     * 登录 cookie
     */
    private List<Header> headers;

}
