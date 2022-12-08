package com.jee.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jee.constants.BilibiliConstants;
import com.jee.entity.UserInfo;
import com.jee.utils.HTTPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * @program: WebVideoDownloader
 * @description:
 * @author: animal
 * @create: 2022-11-27 11:21
 **/
//@Component
@Slf4j
public class BilibiliLoginService {

    @Value("${bilibili.username}")
    private String userName;
    @Value("${bilibili.passWord}")
    private String passWord;
    @Value("${bilibili.token}")
    private String token;
    @Value("${bilibili.challenge}")
    private String challenge;
    @Value("${bilibili.validate}")
    private String validate;
    @Value("${bilibili.seccode}")
    private String seccode;

    @Autowired
    private ObjectMapper objectMapper;

    public void login() throws Exception {
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(passWord)) {
            throw new IllegalArgumentException("userName or passWord is null");
        }

        log.info("start login bilibili, userName:{} passWord:{}", userName, passWord);
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(userName);
        userInfo.setPassword(passWord);

        doLogin(userInfo);
    }

    private void doLogin(UserInfo userInfo) throws Exception {
        String username = userInfo.getUsername();
        String encryptPassword = encryptPassword(userInfo);

        Map<String, String> reqParam = new HashMap<>();
        reqParam.put("username", username);
        reqParam.put("password", encryptPassword);
        reqParam.put("keep", "0");
        reqParam.put("token", token);
        reqParam.put("challenge", challenge);
        reqParam.put("validate", validate);
        reqParam.put("seccode", seccode);
        reqParam.put("source", "main_web");
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("referer", "https://www.bilibili.com/"));
        HttpPost httpPost = HTTPUtils.buildPostReq(BilibiliConstants.BILIBILI_LOGIN_URL, reqParam, headers.toArray(new Header[]{}));
        BasicCookieStore basicCookieStore = new BasicCookieStore();
        HttpClientContext httpClientContext = new HttpClientContext();

        try(
                CloseableHttpClient httpClient = HttpClients.custom()
                        .setDefaultCookieStore(basicCookieStore)
                        .build();
                CloseableHttpResponse httpResponse = httpClient.execute(httpPost, httpClientContext);
                ){

            List<Cookie> cookies = basicCookieStore.getCookies();
            System.out.println(cookies);

            CookieStore cookieStore = httpClientContext.getCookieStore();
            List<Cookie> cookies1 = cookieStore.getCookies();
            System.out.println(cookies1);

            Header[] headers1 = httpResponse.getHeaders("Set-Cookie");
            System.out.println(Arrays.toString(headers1));

            HttpEntity entity = httpResponse.getEntity();
            String respJson = EntityUtils.toString(entity);
            JsonNode jsonNode = objectMapper.readTree(respJson);
            String code = jsonNode.get("code").asText();
            if (!"0".equals(code)) {
                String message = jsonNode.get("message").asText();
                throw new RuntimeException("login failed, errMsg:" + message);
            }

        }
    }

    private String encryptPassword(UserInfo userInfo) throws Exception {

        // 获取公钥和盐值
        HttpGet httpGet = HTTPUtils.buildGetReq(BilibiliConstants.HASH_URL);
        String respJson = null;
        try (
                CloseableHttpResponse httpResponse = HTTPUtils.httpClient.execute(httpGet);
        ) {
            HttpEntity entity = httpResponse.getEntity();
            respJson = EntityUtils.toString(entity);
        }

        JsonNode respJsonNode = objectMapper.readTree(respJson);
        JsonNode dataJson = respJsonNode.get("data");

        String salt = dataJson.get("hash").asText();
        String pubKeyTxt = dataJson.get("key").asText();

        // 读取 PEM 格式公钥
        String[] split = pubKeyTxt.split("\n");
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : split) {
            if (line.contains("PUBLIC KEY") || line.contains("PUBLIC KEY")) {
                continue;
            }
            stringBuilder.append(line);
        }
        byte[] decodePublicKey = Base64.getDecoder().decode(stringBuilder.toString());
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(decodePublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(x509EncodedKeySpec);
        // RSA 加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        byte[] saltedPassword = (salt + userInfo.getPassword()).getBytes();
        byte[] encryptByteArr = cipher.doFinal(saltedPassword);
        String encryptText = Base64.getEncoder().encodeToString(encryptByteArr);
        return encryptText;
    }
}
