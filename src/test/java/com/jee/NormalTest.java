package com.jee;

import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @program: WebVideoDownloader
 * @description:
 * @author: animal
 * @create: 2022-11-27 09:37
 **/
public class NormalTest {

    @Test
    public void test01() throws UnsupportedEncodingException {
        String url = "e=ig8euxZM2rNcNbRVhwdVhwdlhWdVhwdVhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=\\u0026uipk=5\\u0026nbs=1\\u0026deadline=1669519923\\u0026gen=playurlv2\\u0026os=hwo1bv\\u0026oi=3719462605\\u0026trid=5a8e7b941b57424b910e6dea8b379b8fu\\u0026mid=0\\u0026platform=pc\\u0026upsig=d8cc1fad01c60df7d8630b4a1b742ac1\\u0026uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform\\u0026bvc=vod\\u0026nettype=0\\u0026orderid=1,3\\u0026buvid=\\u0026build=0\\u0026agrr=0\\u0026bw=39564\\u0026logo=40000000";
        String code = "UTF-8";
        String decode = URLDecoder.decode(url, code);
        System.out.println(decode);
    }

    @Test
    public void windowTest(){
        JfxApplication.main(new String[0]);
    }
}
