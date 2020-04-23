package com.zcyk.service;

import com.alibaba.fastjson.JSONObject;
import com.zcyk.dto.CameraList;
import com.zcyk.dto.ResultData;

import java.util.HashMap;
import java.util.List;

/**
 * 功能描述：乐橙API
 * 开发人员：Wujiefeng
 * 创建时间：2020/4/13 15:42
 * 参数：[ * @param null]
 * 返回值：
*/
public interface LiveService {
    /*查询设备绑定情况*/
    JSONObject checkDevice(HashMap<String, Object> paraMap,String id);

    /*绑定设备*/
    JSONObject bindDevice(HashMap<String, Object> paramMap, String id);
    /*获取管理员token*/
    String accessToken(HashMap<String, Object> paramMap, String id);
    /*获取设备在线状态*/
//    JSONObject getDevice(HashMap<String, Object> paramMap);
    /*开启直播*/
    JSONObject bindDeviceLive(HashMap<String, Object> paramMap, String id);
    /*获取通道详细信息*/
    JSONObject queryBaseDeviceChannelInfo(HashMap<String, Object> paramMap, String id);
    /*获取直播列表*/
    JSONObject liveList(HashMap<String, Object> paramMap, String id);
    /*修改设备或通道名称*/
    JSONObject modifyDeviceName(HashMap<String, Object>paramMap,String id);
    /*查询设备状态*/
    JSONObject deviceOnline(HashMap<String, Object>paramMap,String app_id,String secret_id);
    /**/
    JSONObject deviceBaseList(HashMap<String,Object>paramMap,String id);

    /*#########################萤石云（海康威视）############################*/

    /*获取Token*/
    String getToken(HashMap<String, Object> paramMap, String id);
    /*获取指定设备通道信息*/
    List<CameraList> getCameraList(HashMap<String, Object> paramMap, String id);
    /*获取用户下直播列表*/
    JSONObject getLiveList(HashMap<String,Object>paramMap,String id);
    /*开启直播*/
    JSONObject openLive(HashMap<String,Object>paramMap);
    /*绑定设备*/
    JSONObject addHkDevice(HashMap<String,Object>paramMap);
    /*修改通道信息*/
    JSONObject updateChannel(HashMap<String,Object>paramMap);
    /*抓拍图片*/
    JSONObject capTure(HashMap<String, Object>paramMap);
    /*获取单个设备信息*/
    JSONObject deviceInfo(HashMap<String, Object>paramMap);
}
