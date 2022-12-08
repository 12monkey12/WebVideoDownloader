package com.jee.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jee.constants.BilibiliConstants;
import com.jee.utils.HTTPUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @program: gradle-demo
 * @description:
 * @author: animal
 * @create: 2022-11-14 15:30
 **/
@Slf4j
@Setter
@Getter
@ToString
public class BilibiliVideoInfo {


    /**
     * 视频信息
     */
    private JsonNode infoJsonNode;
    /**
     * 下载信息响应 json
     */
    private JsonNode playerJsonNode;

    /**
     * 原始url
     */
    private String originUrl;
    /**
     * 稿件bvid
     */
    private String bvid;
    /**
     * 稿件avid
     */
    private String avid;
    /**
     * 视频1P cid
     */
    private String cid;
    /**
     * 视频标题
     */
    private String title;

    /**
     * 视频下载地址
     */
    private String videoUrl;
    /**
     * 音频下载地址
     */
    private String audioUrl;

}
