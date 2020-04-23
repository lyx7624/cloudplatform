package com.zcyk.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dahua.openapi.util.MySecureProtocolSocketFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.io.IOUtils;

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
        ProtocolSocketFactory factory = new MySecureProtocolSocketFactory();

            Protocol.registerProtocol("https", new Protocol("https", factory, 443));
            HttpClient client = new HttpClient();
            client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
            PostMethod method = new PostMethod(url);
            System.out.println(url);
            JSONObject jsonObject = new JSONObject();
            try {
                RequestEntity entity = new StringRequestEntity(json, "application/json", "UTF-8");
                method.setRequestEntity(entity);
                client.executeMethod(method);

                InputStream inputStream = method.getResponseBodyAsStream();
                String result = IOUtils.toString(inputStream, "UTF-8");
                jsonObject = JSONObject.parseObject(result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                method.releaseConnection();
            }
            return jsonObject;

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
