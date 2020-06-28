package com.zcyk.util;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 功能描述: resqust工具类
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/8/1 17:16
 */
public class ResponseUtil {

    public static void responseJson(HttpServletRequest request, HttpServletResponse response, int status, Object data) {
        try {
			/*;
			response.setHeader("Access-Control-Expose-Headers", "DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range,Access-ZZJ_Token,status");
			*/
            response.setHeader("Access-Control-Allow-Headers", "Content-Type");
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, HEAD");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setStatus(status);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSONObject.toJSONString(data));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void responseAddHeader(Map<String,String> headers,HttpServletRequest request, HttpServletResponse response, int status, Object data) {
        try {

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                response.addHeader(entry.getKey(),entry.getValue());
            }
			response.setHeader("Access-Control-Expose-Headers", "DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range,user_token");
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setStatus(status);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSONObject.toJSONString(data));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
