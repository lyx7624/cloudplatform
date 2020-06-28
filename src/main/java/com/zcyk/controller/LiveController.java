package com.zcyk.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zcyk.dto.CameraList;
import com.zcyk.dto.DeviceList;
import com.zcyk.dto.LiveList;
import com.zcyk.dto.ResultData;
import com.zcyk.pojo.LiveApp;
import com.zcyk.service.LiveAppService;
import com.zcyk.service.LiveService;
import com.zcyk.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author WuJieFeng
 * @date 2020/4/13 15:43
 */
@RestController
@Slf4j
@RequestMapping("/LCipc")
public class LiveController {

    @Autowired
    LiveService liveService;
    @Autowired
    LiveAppService liveAppService;


    /**
     * 功能描述：绑定应用
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/14 11:23
     * 参数：[ * @param null]
     * 返回值：
    */
    @RequestMapping("addLeCheng")
    public ResultData addLeCheng(LiveApp liveApp) throws Exception {
        if (liveApp.getType()==1) {//如果是大华应用
            HashMap<String, Object> paramMap = new HashMap<>();
            String method = "accessToken";
            JSONObject json = HttpSend.execute(paramMap, method, liveApp.getApp_id(), liveApp.getSecret_id());
            JSONObject jsonResult = json.getJSONObject("result");
            String code = jsonResult.getString("code");
            String msg = jsonResult.getString("msg");
            if (code.equals("0")) {//判断能否获取token
                JSONObject data = jsonResult.getJSONObject("data");
                String accessToken = data.getString("accessToken");
                HashMap<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("deviceId",liveApp.getDevice_id());
                paramsMap.put("token",accessToken);
                JSONObject jsonObject = liveService.deviceOnline(paramsMap, liveApp.getApp_id(), liveApp.getSecret_id());//判断设备序列号是否正确
                JSONObject result = jsonObject.getJSONObject("result");
                String code1 = result.getString("code");
                String msg1 = result.getString("msg");
                if(!code1.equals("0")){
                     return new ResultData().setStatus("402").setMsg(msg1);
                }
                return liveAppService.addAPP(liveApp.setAccess_token(accessToken).setToken_time(new Date()));
            }
            return new ResultData().setMsg("msg").setStatus("401");
        }else {//海康设备
            HashMap<String,Object> paramsMap = new HashMap<>();
            String app_id = liveApp.getApp_id();
            String secret_id = liveApp.getSecret_id();
            paramsMap.put("appKey",app_id);
            paramsMap.put("appSecret",secret_id);
            String content = null;
            try {
                    content = HttpClientUtils.getContent("https://open.ys7.com/api/lapp/token/get", paramsMap);
                } catch (Exception e) {//
                    log.error("绑定海康应用失败：",e);
                    throw new Exception("海康远程服务器错误，请稍后重试");
            }
            JSONObject json = JSONObject.parseObject(content);
            String code = json.getString("code");
            String msg = json.getString("msg");
            if (code.equals("200")) {//验证是否能获取token
                JSONObject jsonData = json.getJSONObject("data");
                String accessToken = jsonData.getString("accessToken");
                HashMap<String,Object> paramMap = new HashMap<>();
                paramMap.put("accessToken",accessToken);
                paramMap.put("deviceSerial",liveApp.getDevice_id());
                JSONObject jsonObject = liveService.deviceInfo(paramMap);//验证设备序列号是否正确
                String code1 = jsonObject.getString("code");
                String msg1 = jsonObject.getString("msg");
                if(!code1.equals("200")){
                    return new ResultData().setStatus("402").setMsg(msg1);
                }
                return liveAppService.addAPP(liveApp.setAccess_token(accessToken).setToken_time(new Date()));
            }
            return new ResultData().setStatus("401").setMsg(msg);
        }
    }

    /**
     * 功能描述：查询应用
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/15 10:55
     * 参数：[ * @param null]
     * 返回值：
    */
    @RequestMapping("getIPCApp")
    public ResultData get(String project_id){
        List<LiveApp> liveApps = liveAppService.selectAppByProjectId(project_id);
        return new ResultData().setData(liveApps).setStatus("200").setMsg("成功");
    }

    /**
     * 功能描述：删除应用
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/21 16:09
     * 参数：[ * @param null]
     * 返回值：
    */
    @RequestMapping("deleteApp")
    public ResultData deleteApp(String id){
        try {
            ResultData resultData = liveAppService.deleteAppById(id);
            return resultData;
        }catch (Exception e){
            e.printStackTrace();
            return new ResultData().setStatus("400").setMsg("失败").setData(e.toString());
        }
    }


    /**
     * 功能描述：更改应用名称
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/21 16:17
     * 参数：[ * @param null]
     * 返回值：
    */
    @RequestMapping("updateAppName")
    public ResultData updateAppName(String id,String app_name){
        try {
            ResultData resultData = liveAppService.updateAppName(id,app_name);
            return resultData;
        }catch (Exception e){
            e.printStackTrace();
            return new ResultData().setStatus("400").setMsg("失败").setData(e.toString());
        }
    }





    /*#########################乐橙云（大华）############################*/


    /**
     * 功能描述：绑定设备(乐橙云)
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/14 10:54
     * 参数：[ * @param null]
     * 返回值：
    */
    @RequestMapping("/bindDevice")
    public ResultData bindDevice(String device_id,String device_code,String id) {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        LiveApp liveApp = liveAppService.selectAppById(id);
        paramsMap.put("token",liveApp.getAccess_token());
        paramsMap.put("deviceId",device_id);
        paramsMap.put("code",device_code);
        JSONObject json = liveService.bindDevice(paramsMap,id);
        JSONObject jsonResult = json.getJSONObject("result");
        String code1 = jsonResult.getString("code");
        String msg = jsonResult.getString("msg");
        if (!code1.equals("0")){
            return new ResultData().setStatus("400").setMsg(msg);
        }
        return new ResultData().setStatus("200").setMsg(msg);
    }

    /**
     * 功能描述：获取管理员Token
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/13 15:44
     * 参数：[ * @param null]
     * 返回值：
    */
    @RequestMapping("/getAccessToken")
    public ResultData getAccessToken(String id){
       // 注：执行main函数前请先将CONST.java文件中的APPID、SECRET以及PHONE填全，不然程序执行将会报错。
        // 获取管理员token
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        String token = liveService.accessToken(paramsMap,id);
        System.out.println(token);
        ResultData resultData = liveAppService.updateToken(new LiveApp().setId(id).setAccess_token(token));
        return resultData;
    }
    
//    /**
//     * 功能描述：获取设备在线状态
//     * 开发人员：Wujiefeng
//     * 创建时间：2020/4/13 15:50
//     * 参数：[ * @param null]
//     * 返回值：
//    */
//    @RequestMapping("/getDeviceOnline")
//    public ResultData getDeviceOnline(String device_id){
//        HashMap<String,Object> paramsMap = new HashMap<>();
//        paramsMap.put("deviceId","5E07090PAZC850B");
//        paramsMap.put("token","At_0000441db7b5812e4424b793fb2358a8");
//        JSONObject json = liveService.getDevice(paramsMap);
//        JSONObject jsonResult = json.getJSONObject("result");
//        JSONObject jsonData = jsonResult.getJSONObject("data");
//        String deviceId = jsonData.getString("deviceId");
//        String onLine = jsonData.getString("onLine");
//        String channels = jsonData.getString("channels");
//        List<Channel> channelList = (List<Channel>) JSONArray.parseArray(channels,Channel.class);
//        String code = jsonResult.getString("code");
//        String msg = jsonResult.getString("msg");
//
//        HashMap<String,Object> map = new HashMap<>();
//        map.put("deviceId",device_id);
//        map.put("onLine",onLine);
//        map.put("channels",channelList);
//        if (!code.equals("0")){
//            return new ResultData().setStatus("400").setMsg(msg);
//        }
//        return new ResultData().setStatus("200").setMsg(msg).setData(map);
//    }
    
    /**
     * 功能描述：指定设备开启直播
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/13 17:01
     * 参数：[ * @param null]
     * 返回值： 
    */
    @RequestMapping("/bindDeviceLive")
    public ResultData bindDeviceLive(String id,String channel){

        LiveApp liveApp = liveAppService.selectAppById(id);
        //获取直播列表
        HashMap<String,Object> paramsMap1 = new HashMap<>();
        paramsMap1.put("token", liveApp.getAccess_token());
        paramsMap1.put("queryRange","1-99");
        JSONObject jsonlive = liveService.liveList(paramsMap1, id);
        JSONObject result = jsonlive.getJSONObject("result");
        String code1 = result.getString("code");
        String msg1 = result.getString("msg");
        if (!code1.equals("0")){
            return new ResultData().setStatus("400").setMsg(msg1);
        }
        JSONObject data = result.getJSONObject("data");
        String lives = data.getString("lives");
        List<LeChengLive> leChengLives = JSONObject.parseArray(lives, LeChengLive.class);
        if (leChengLives.size()!=0){
           for(LeChengLive leChengLive:leChengLives ){
               if (channel.equals(leChengLive.getChannelId())){
                   return new ResultData().setData(leChengLive).setStatus("200").setMsg("成功");
               }
           }
        }

        HashMap<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("deviceId", liveApp.getDevice_id());
        paramsMap.put("token", liveApp.getAccess_token());
        paramsMap.put("channelId",channel);
        paramsMap.put("streamId",0);
        paramsMap.put("liveMode","proxy");
        JSONObject json = liveService.bindDeviceLive(paramsMap,id);
        JSONObject jsonResult = json.getJSONObject("result");
        String code = jsonResult.getString("code");
        String msg = jsonResult.getString("msg");
        if (!code.equals("0")){
            return new ResultData().setStatus("400").setMsg(msg);
        }
        JSONObject jsonData = jsonResult.getJSONObject("data");
        Integer liveStatus = jsonData.getInteger("liveStatus");
        String streams = jsonData.getString("streams");
        List<Stream> streamList = JSONArray.parseArray(streams,Stream.class);
        HashMap<String,Object> map = new HashMap<>();
        map.put("liveStatus",liveStatus);
        map.put("streams",streamList);
        return new ResultData().setStatus("200").setMsg(msg).setData(map);
    }
   /**
    * 功能描述：获取通道信息
    * 开发人员：Wujiefeng
    * 创建时间：2020/4/14 9:33
    * 参数：[ * @param null]
    * 返回值：
   */
   @RequestMapping("/queryOpenDevice")
   public ResultData queryBaseDeviceChannelInfo(String project_id,String id){
       List<DeviceList> deviceLists = deviceBaseList(id);
       ArrayList<Object> objects = new ArrayList<>();
       LiveApp liveApp = liveAppService.selectAppById(id);
       String token = liveApp.getAccess_token();
       for (DeviceList deviceList:deviceLists){
          objects.add(deviceList.getDeviceId());
       }

       String join = StringUtils.join(objects, ",");
       System.out.println(join);
       HashMap<String,Object> paramsMap = new HashMap<>();
       paramsMap.put("deviceIds",join);
       paramsMap.put("token",token);
       JSONObject json = liveService.queryBaseDeviceChannelInfo(paramsMap,id);
       JSONObject jsonResult = json.getJSONObject("result");
       String code = jsonResult.getString("code");
       String msg = jsonResult.getString("msg");
       if(!code.equals("0")){
           return new ResultData().setStatus("400").setMsg(msg);
       }
       JSONObject jsonData = jsonResult.getJSONObject("data");
       String devices = jsonData.getString("devices");
       List<Device>deviceList = JSONArray.parseArray(devices,Device.class);
       HashMap<String,Object> map = new HashMap<>();
       map.put("devices",deviceList);

       return new ResultData().setStatus("200").setMsg(msg).setData(map);
   }

   /**
    * 功能描述：修改设备或通道名称
    * 开发人员：Wujiefeng
    * 创建时间：2020/4/21 16:22
    * 参数：[ * @param null]
    * 返回值：
   */
   @RequestMapping("/updateDHdevice")
   public ResultData updateDHdevice(String name,String id,String device_id,@RequestParam(defaultValue = "") String channelId){
       HashMap<String, Object> paramsMap = new HashMap<String, Object>();
       LiveApp liveApp = liveAppService.selectAppById(id);
       paramsMap.put("token",liveApp.getAccess_token());
       paramsMap.put("deviceId",device_id);
       paramsMap.put("channelId",channelId);
       paramsMap.put("name",name);
       JSONObject jsonObject = liveService.modifyDeviceName(paramsMap, id);
       JSONObject resultData = jsonObject.getJSONObject("result");
       String code = resultData.getString("code");
       String msg = resultData.getString("msg");
       if(!code.equals("0")){
           return new ResultData().setStatus("400").setMsg(msg);
       }
       return new ResultData().setStatus("200").setMsg("修改成功");
   }

    /**
     * 功能描述：获取设备
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/22 10:17
     * 参数：[ * @param null]
     * 返回值：
     */
    public List<DeviceList> deviceBaseList(String id){
        HashMap<String,Object> paramsMap = new HashMap<>();
        LiveApp liveApp = liveAppService.selectAppById(id);
        Boolean b =false;
        paramsMap.put("token",liveApp.getAccess_token());
        paramsMap.put("bindId",-1);
        paramsMap.put("limit",128);
        paramsMap.put("type","bind");
        paramsMap.put("needApInfo",b);
        JSONObject jsonObject = liveService.deviceBaseList(paramsMap, id);
        JSONObject jsonResult = jsonObject.getJSONObject("result");
        JSONObject jsonData = jsonResult.getJSONObject("data");
        String deviceList = jsonData.getString("deviceList");
        List<DeviceList> deviceLists = JSONObject.parseArray(deviceList, DeviceList.class);

        return deviceLists;
    }

    /*#########################萤石云（海康威视）############################*/

    /**
     * 功能描述：获取token
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/17 13:50
     * 参数：[ * @param null]
     * 返回值：
    */
    @RequestMapping("/HkgetToken")
    public ResultData getToken(String id){
        HashMap<String,Object> paramsMap = new HashMap<>();
        String token = liveService.getToken(paramsMap, id);
        ResultData resultData = liveAppService.updateToken(new LiveApp().setId(id).setAccess_token(token));
        return resultData;
    }



    /**
     * 功能描述：获取指定设备通道信息
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/20 10:42
     * 参数：[ * @param null]
     * 返回值：
    */
//    @RequestMapping("/getCameraList")
    public List<CameraList> getCameraList(String id){
        HashMap<String,Object> paramsMap = new HashMap<>();
        List<CameraList> cameraLists = liveService.getCameraList(paramsMap, id);
        return cameraLists;
    }

    /**
     * 功能描述：获取用户下直播列表
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/20 11:19
     * 参数：[ * @param null]
     * 返回值：
    */
    @RequestMapping("getLiveList")
    public ResultData getLiveList(String id){
        HashMap<String,Object> paramsMap = new HashMap<>();
        JSONObject json = liveService.getLiveList(paramsMap,id);
        String data = json.getString("data");
        List<LiveList> liveLists = JSONObject.parseArray(data, LiveList.class);

        List<CameraList> cameraLists = getCameraList(id);
        for(CameraList cameraList:cameraLists){
            String device_id = cameraList.getDeviceSerial();
            Integer channelNo = cameraList.getChannelNo();
            for (LiveList liveList:liveLists){
                String d_id = liveList.getDeviceSerial();
                Integer c_no = liveList.getChannelNo();
                if(device_id.equals(d_id)&&channelNo==c_no){
                    liveList.setChannel_name(cameraList.getChannelName());
                    liveList.setPic_url(cameraList.getPicUrl());
                }
            }
        }
        return new ResultData().setMsg("成功").setStatus("200").setData(liveLists);
    }

    /**
     * 功能描述：开启直播
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/20 13:45
     * 参数：[ * @param null]
     * 返回值：
    */
    @RequestMapping("openLive")
    public ResultData openLive(String id,String channel){
        HashMap<String,Object> paramsMap = new HashMap<>();
        LiveApp liveApp = liveAppService.selectAppById(id);
        paramsMap.put("accessToken", liveApp.getAccess_token());
        paramsMap.put("source", liveApp.getDevice_id()+":"+channel);
        JSONObject jsonObject = liveService.openLive(paramsMap);
        String data = jsonObject.getString("data");
        return new ResultData().setStatus("200").setMsg("成功").setData(data);
    }

    /**
     * 功能描述：绑定设备
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/21 10:21
     * 参数：[ * @param null]
     * 返回值：
    */
    @RequestMapping("/addHkDevice")
    public ResultData addHkDevice(String id,String device_id, String device_code){
        HashMap<String,Object> paramsMap = new HashMap<>();
        LiveApp liveApp = liveAppService.selectAppById(id);
        paramsMap.put("accessToken",liveApp.getAccess_token());
        paramsMap.put("deviceSerial",device_id);
        paramsMap.put("validateCode",device_code);
        JSONObject jsonObject = liveService.addHkDevice(paramsMap);
        String code = jsonObject.getString("code");
        String msg = jsonObject.getString("msg");
        if (code.equals("200")){
            return new ResultData().setStatus("200").setMsg(msg);
        }
        return new ResultData().setStatus("400").setMsg(msg);
    }

    /**
     * 功能描述：修改通道名称
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/21 10:32
     * 参数：[ * @param null]
     * 返回值：
    */
    @RequestMapping("updateHKChannel")
    public ResultData updateChannel(String id,String name,@RequestParam(defaultValue = "1") int channelId,String device_id){
        HashMap<String,Object> paramsMap = new HashMap<>();
        LiveApp liveApp = liveAppService.selectAppById(id);
        paramsMap.put("accessToken",liveApp.getAccess_token());
        paramsMap.put("deviceSerial",device_id);
        paramsMap.put("name",name);
        paramsMap.put("channelNo",channelId);
        JSONObject jsonObject = liveService.updateChannel(paramsMap);
        String code = jsonObject.getString("code");
        String msg = jsonObject.getString("msg");
        if (code.equals("200")){
            return new ResultData().setStatus("200").setMsg(msg);
        }
        return new ResultData().setStatus("400").setMsg(msg);

    }

    /**
     * 功能描述：抓拍图片
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/21 14:45
     * 参数：[ * @param null]
     * 返回值：
    */
    @RequestMapping("/capTure")
    public ResultData capTure(String id){
        HashMap<String,Object> paramsMap = new HashMap<>();
        LiveApp liveApp = liveAppService.selectAppById(id);
        paramsMap.put("accessToken",liveApp.getAccess_token());
        paramsMap.put("deviceSerial",liveApp.getDevice_id());
        paramsMap.put("channelNo",1);
        JSONObject jsonObject = liveService.capTure(paramsMap);
        JSONObject data = jsonObject.getJSONObject("data");
        String picUrl = data.getString("picUrl");
        return new ResultData().setStatus("200").setMsg("成功").setData(picUrl);

    }

}
