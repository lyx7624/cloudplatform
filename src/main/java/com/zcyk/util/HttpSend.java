package com.zcyk.util;






import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dahua.openapi.util.MySecureProtocolSocketFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
/**
 * 功能描述：乐橙API
 * 开发人员：Wujiefeng
 * 创建时间：2020/4/13 15:36
 * 参数：[ * @param null]
 * 返回值：
*/
public class HttpSend {

    private static final CloseableHttpClient httpClient;
    public static final String CHARSET = "UTF-8";
    // 采用静态代码块，初始化超时时间配置，再根据配置生成默认httpClient对象
    static {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(15000).build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }

    public static JSONObject execute(Map<String, Object> paramsMap, String method,String app_id,String secret_id) {
            Map<String, Object> map = paramsInit(paramsMap, app_id, secret_id);
            // 返回json
            JSONObject jsonObj = doPost("https://openapi.lechange.cn" + ":" + "443" + "/openapi/" + method, map);
            System.out.println("=============================");
            System.out.println("返回结果：" + jsonObj.toJSONString());
            return jsonObj;

    }

    public static JSONObject doPost(String url, Map<String, Object> map) {
        String json = JSON.toJSONString(map);
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return JSONObject.parseObject(resultString);

    }

    protected static Map<String, Object> paramsInit(Map<String, Object> paramsMap,String app_id,String secret_id) {
        long time = System.currentTimeMillis() / 1000;
        String nonce = UUID.randomUUID().toString();
        String id = UUID.randomUUID().toString();

        StringBuilder paramString = new StringBuilder();
        paramString.append("time:").append(time).append(",");
        paramString.append("nonce:").append(nonce).append(",");
        paramString.append("appSecret:").append(secret_id);

        String sign = "";
        // 计算MD5得值
        try {
            System.out.println("传入参数：" + paramString.toString().trim());
            sign = DigestUtils.md5Hex(paramString.toString().trim().getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> systemMap = new HashMap<String, Object>();
        systemMap.put("ver", "1.0");
        systemMap.put("sign", sign);
        systemMap.put("appId", app_id);
        systemMap.put("nonce", nonce);
        systemMap.put("time", time);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("system", systemMap);
        map.put("params", paramsMap);
        map.put("id", id);
        return map;
    }

}
