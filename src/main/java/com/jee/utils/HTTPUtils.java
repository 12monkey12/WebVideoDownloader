package com.jee.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: gradle-demo
 * @description:
 * @author: animal
 * @create: 2022-11-21 21:22
 **/
@Slf4j
public class HTTPUtils {

    public static final CloseableHttpClient httpClient = HttpClients.createDefault();

    private static String contentType = "video/mp4";

    public static HttpGet buildGetReq(String url, Map<String, String> params, Header[] headers) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(url);
        // 拼接请求参数
        if (MapUtils.isNotEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                uriBuilder.setParameter(key, value);
            }
        }

        URI build = uriBuilder.build();
        log.info("requestUrl:{}", build.toString());
        HttpGet httpGet = new HttpGet(build);

        // 填充标头
        if (ArrayUtils.isNotEmpty(headers)) {
            httpGet.setHeaders(headers);
        }

        return httpGet;
    }

    public static HttpGet buildGetReq(String url) throws URISyntaxException {
        return buildGetReq(url, null, null);
    }

    public static HttpPost buildPostReq(String url, Map<String, String> params, Header[] headers) throws URISyntaxException, UnsupportedEncodingException {
        log.info("requestUrl:{}", url);
        HttpPost httpPost = new HttpPost(url);

        // 请求参数
        if (MapUtils.isNotEmpty(params)) {
            List<NameValuePair> formParams = new ArrayList<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                formParams.add(new BasicNameValuePair(key, value));
            }
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formParams);
            httpPost.setEntity(urlEncodedFormEntity);
        }

        // 标头
        if (ArrayUtils.isNotEmpty(headers)) {
            httpPost.setHeaders(headers);
        }

        return httpPost;
    }

    public static String httpGetToString(HttpGet httpGet) throws IOException {
        String respJson = null;
        try (
                CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        ) {
            HttpEntity entity = httpResponse.getEntity();
            respJson = EntityUtils.toString(entity);
        }
        return respJson;
    }

    public static byte[] httpGetToByteArray(HttpGet httpGet) throws IOException {
        byte[] respJson = null;
        try (
                CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        ) {
            HttpEntity entity = httpResponse.getEntity();
            respJson = EntityUtils.toByteArray(entity);
        }
        return respJson;
    }
}
