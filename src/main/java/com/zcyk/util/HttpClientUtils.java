package com.zcyk.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;


import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * 基于 httpclient 4.5版本的 http工具类
 */

public class HttpClientUtils {
    private static final CloseableHttpClient httpClient;
    public static final String CHARSET = "UTF-8";
    // 采用静态代码块，初始化超时时间配置，再根据配置生成默认httpClient对象
    static {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(15000).build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }




    /**
     * 功能描述：带认证，get请求参数在url后面
     * 开发人员： lyx
     * 创建时间： 2019/12/26 14:01
     * 参数：
     * 返回值：
     * 异常：
     */
    public static String doGet(String url, Map<String, String> params, String authorization,String contentType) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                // 将请求参数和url进行拼接
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, CHARSET));
            }
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Content-type", contentType==null?"\"application/json;charset=UTF-8\"":contentType);
            httpGet.setHeader("Authorization", "Bearer "+authorization);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 功能描述：带认证，post请求参数在请求中
     * 开发人员： lyx
     * 创建时间： 2019/12/26 14:01
     * 参数：
     * 返回值：
     * 异常：
     */
    public static String doPost(String url, Map<String, Object> params, String authorization,String contentType)
            throws IOException {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        List<NameValuePair> pairs = null;
        if (params != null && !params.isEmpty()) {
            pairs = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String value = entry.getValue().toString();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-type", contentType==null?"\"application/json;charset=UTF-8\"":contentType);
        httpPost.setHeader("Authorization",authorization==null?"":"Bearer "+authorization);


        if (pairs != null && pairs.size() > 0) {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
        }
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (response != null)
                response.close();
        }
        return null;
    }

    /**
     * 功能描述：不带认证，post请求参数在请求中以json格式
     * 开发人员： lyx
     * 创建时间： 2019/12/26 14:01
     * 参数：
     * 返回值：
     * 异常：
     */
    public static String doPostJson(String url, String json) {
        // 创建Httpclient对象
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

        return resultString;
    }

    /**
     * 功能描述：带认证，post请求参数在请求中以json格式
     * 开发人员： lyx
     * 创建时间： 2019/12/26 14:01
     * 参数：
     * 返回值：
     * 异常：
     */
    public static String doPostJson(String url, String json,String contentType,String authorization) {
        // 创建Httpclient对象
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-type", contentType==null?"application/json":contentType);
            httpPost.setHeader("Authorization",authorization==null?"":"Bearer "+authorization);
            // 执行http请求
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
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

        return resultString;
    }

    /**
     * 功能描述：posturl参数
     * 开发人员： lyx
     * 创建时间： 2019/11/19 10:37
     * 参数：
     * 返回值：
     * 异常：
     */
    public static String doPostUrl(String url, Map<String, String> params,String authorization) {
        // 创建Httpclient对象
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                // 将请求参数和url进行拼接
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, CHARSET));
            }
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Authorization","Bearer "+authorization);

            // 创建请求内容
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

        return resultString;
    }


    /**
     * 功能描述：带认证，post表格方式上传文件，和参数
     * 开发人员： lyx
     * 创建时间： 2019/12/26 14:17
     * 参数：
     * 返回值：
     * 异常：
     */
    public static String postFile(File file, String url, Map<String, String> params,String authorization) throws Exception {

        if (StringUtils.isBlank(url)) {
            return null;
        }
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        String result = null;
        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000*3).setConnectTimeout(15000*3).setConnectionRequestTimeout(60000*3).build();
            httpclient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Authorization","Bearer "+authorization);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            //解决上传文件，文件名中文乱码问题
            builder.setCharset(Charset.forName("utf-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//设置浏览器兼容模式
            // builder.addBinaryBody("uploadFile", file, ContentType.create("multipart/form-data"), "file");
            if (params != null && !params.isEmpty()) {//设置参数
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    Object value = entry.getValue();
                    if (value != null) {
                        StringBody stringBody = new StringBody(entry.getValue(), ContentType.create("text/plain", Consts.UTF_8));
                        builder.addPart(entry.getKey(), stringBody);
                    }
                }
            }

            //将java.io.File对象添加到HttpEntity（org.apache.http.HttpEntity）对象中
            if(file!=null){
                builder.addPart("Chunk", new FileBody(file));
            }
            httpPost.setEntity(builder.build());
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("网络错误");
        }  finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /**
     * 功能描述：海康威视post请求
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/20 9:09
     * 参数：[ * @param null]
     * 返回值：
    */
    public static String getContent(String url, Map<String, Object> mapdata){
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httpPost = new HttpPost(url);
        try {
            // 设置提交方式
            httpPost.addHeader("Content-type", "application/x-www-form-urlencoded; charset=utf-8");
            // 添加参数
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            if (mapdata.size() != 0) {
                // 将mapdata中的key存在set集合中，通过迭代器取出所有的key，再获取每一个键对应的值
                Set keySet = mapdata.keySet();
                Iterator it = keySet.iterator();
                while (it.hasNext()) {
                    String k =  it.next().toString();// key
                    String v = mapdata.get(k).toString();// value
                    nameValuePairs.add(new BasicNameValuePair(k, v));
                }
            }
            httpPost.setEntity( new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
            // 执行http请求
            response = httpClient.execute(httpPost);
            // 获得http响应体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // 响应的结果
                String content = EntityUtils.toString(entity, "UTF-8");
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "获取数据错误";
    }

}
