package com.zcyk.service.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.zcyk.dto.CameraList;
import com.zcyk.mapper.LiveAppMapper;
import com.zcyk.pojo.LiveApp;
import com.zcyk.service.LiveService;
import com.zcyk.util.HttpClientUtils;
import com.zcyk.util.HttpSend;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author WuJieFeng
 * @date 2020/4/13 15:33
 */
@Service
@Transactional
public class LiveServiceImpl implements LiveService {

    @Autowired
    LiveAppMapper deviceMapper;

    @Override
    public String accessToken(HashMap<String, Object> paramMap,String id) {
        LiveApp liveApp = deviceMapper.selectByPrimaryKey(id);
        String  access_token = liveApp.getAccess_token();
        Date token_time = null;
        Date nowDate = new Date();
        boolean b = StringUtils.isBlank(access_token);
        if (liveApp.getToken_time()==null) {
              token_time= new Date();
        }else {
              token_time= liveApp.getToken_time();
        }
        boolean time = nowDate.getTime()-token_time.getTime() >3*24*60*60*1000;
        if (b||time){
            String app_id = liveApp.getApp_id();
            String secret_id = liveApp.getSecret_id();
            String method = "accessToken";
            JSONObject json = HttpSend.execute(paramMap, method, app_id, secret_id);
            JSONObject jsonResult = json.getJSONObject("result");
            JSONObject jsonData = jsonResult.getJSONObject("data");
            String token = jsonData.getString("accessToken");
            deviceMapper.updateByPrimaryKeySelective(new LiveApp().setId(id).setToken_time(new Date()));
            return token;
        }
        return access_token;

    }

    @Override
    public JSONObject checkDevice(HashMap<String, Object> paraMap,String id){
        LiveApp liveApp = deviceMapper.selectByPrimaryKey(id);
        String app_id = liveApp.getApp_id();
        String secret_id = liveApp.getSecret_id();
        String method = "checkDeviceBindOrNot";
        return HttpSend.execute(paraMap,method,app_id,secret_id);

    }

//    @Override
//    public JSONObject getDevice(HashMap<String, Object> paramMap){
//        String method = "deviceOnline";
//        return HttpSend.execute(paramMap,method);
//    }

    @Override
    public JSONObject bindDeviceLive(HashMap<String, Object> paramMap,String id){
        LiveApp liveApp = deviceMapper.selectByPrimaryKey(id);
        String app_id = liveApp.getApp_id();
        String secret_id = liveApp.getSecret_id();
        String method = "bindDeviceLive";
        return HttpSend.execute(paramMap,method,app_id,secret_id);
    }

    @Override
    public JSONObject queryBaseDeviceChannelInfo(HashMap<String, Object> paramMap,String id){
        LiveApp liveApp = deviceMapper.selectByPrimaryKey(id);
        String app_id = liveApp.getApp_id();
        String secret_id = liveApp.getSecret_id();
        String method = "queryBaseDeviceChannelInfo";
        return HttpSend.execute(paramMap,method,app_id,secret_id);
    }

    @Override
    public JSONObject bindDevice(HashMap<String, Object> paramMap,String id){
        LiveApp liveApp = deviceMapper.selectByPrimaryKey(id);
        String app_id = liveApp.getApp_id();
        String secret_id = liveApp.getSecret_id();
        String method = "bindDevice";
        return HttpSend.execute(paramMap,method,app_id,secret_id);
    }

    @Override
    public JSONObject liveList(HashMap<String, Object> paramMap, String id){
        LiveApp liveApp = deviceMapper.selectByPrimaryKey(id);
        String app_id = liveApp.getApp_id();
        String secret_id = liveApp.getSecret_id();
        String method = "liveList";
        return HttpSend.execute(paramMap,method,app_id,secret_id);
    }

    @Override
    public JSONObject modifyDeviceName(HashMap<String, Object>paramMap,String id){
        LiveApp liveApp = deviceMapper.selectByPrimaryKey(id);
        String app_id = liveApp.getApp_id();
        String secret_id = liveApp.getSecret_id();
        String method = "modifyDeviceName";
        return HttpSend.execute(paramMap,method,app_id,secret_id);
    }

    @Override
    public JSONObject deviceOnline(HashMap<String, Object>paramMap,String app_id,String secret_id){
        String method = "deviceOnline";
        return HttpSend.execute(paramMap,method,app_id,secret_id);
    }

    @Override
    public JSONObject deviceBaseList(HashMap<String,Object>paramMap,String id){
        String method = "deviceBaseList";
        LiveApp liveApp = deviceMapper.selectByPrimaryKey(id);
        String app_id = liveApp.getApp_id();
        String secret_id = liveApp.getSecret_id();
        return HttpSend.execute(paramMap,method,app_id,secret_id);
    }


    /*#########################萤石云（海康威视）############################*/

    String url = "https://open.ys7.com/api/lapp/";
    @Override
    public String getToken(HashMap<String, Object>paramMap, String id){
        LiveApp liveApp = deviceMapper.selectByPrimaryKey(id);
        String  token = liveApp.getAccess_token();
        Date token_time = null;
        Date nowDate = new Date();
        boolean b = StringUtils.isBlank(token);
        if (liveApp.getToken_time()==null) {
            token_time= new Date();
        }else {
            token_time= liveApp.getToken_time();
        }
        boolean time = nowDate.getTime()-token_time.getTime() >7*24*60*60*1000;
        if (b||time) {
            String app_id = liveApp.getApp_id();
            String secret_id = liveApp.getSecret_id();

            paramMap.put("appKey",app_id);
            paramMap.put("appSecret",secret_id);
            String content = HttpClientUtils.getContent("https://open.ys7.com/api/lapp/token/get", paramMap);
            JSONObject json = JSONObject.parseObject(content);
            JSONObject jsonData = json.getJSONObject("data");
            String accessToken = jsonData.getString("accessToken");
            System.out.println("API海康摄像头token："+accessToken);
            deviceMapper.updateByPrimaryKeySelective(new LiveApp().setId(id).setToken_time(new Date()));
            return accessToken;
        }
        System.out.println("数据库海康摄像头token："+token);
        return token;
    }

    @Override
    public List<CameraList> getCameraList(HashMap<String, Object> paramMap, String id){
        LiveApp liveApp = deviceMapper.selectByPrimaryKey(id);
        List<CameraList> cameraLists = new ArrayList<>();
        List<LiveApp> liveApps = deviceMapper.selectLiveAppByToken(liveApp.getAccess_token());
        for(LiveApp liveApp1:liveApps) {
            String token = liveApp.getAccess_token();
            paramMap.put("accessToken", token);
            paramMap.put("deviceSerial", liveApp1.getDevice_id());
            String content = HttpClientUtils.getContent("https://open.ys7.com/api/lapp/device/camera/list", paramMap);
            JSONObject json = JSONObject.parseObject(content);
            String data = json.getString("data");
            List<CameraList> one = JSONObject.parseArray(data, CameraList.class);
            for (CameraList cameraList:one){
                cameraLists.add(cameraList);
            }
        }
        return cameraLists;
    }

    @Override
    public JSONObject getLiveList(HashMap<String,Object>paramMap,String id){
        LiveApp liveApp = deviceMapper.selectByPrimaryKey(id);
        paramMap.put("accessToken", liveApp.getAccess_token());
        paramMap.put("pageStart",0);
        paramMap.put("pageSize",50);
        String content = HttpClientUtils.getContent("https://open.ys7.com/api/lapp/live/video/list", paramMap);
        JSONObject json = JSONObject.parseObject(content);
        System.out.println(json);
        return json;
    }

    @Override
    public JSONObject openLive(HashMap<String,Object>paramMap){
        String content = HttpClientUtils.getContent("https://open.ys7.com/api/lapp/live/video/open", paramMap);
        JSONObject jsonObject = JSONObject.parseObject(content);
        return jsonObject;
    }

    @Override
    public JSONObject addHkDevice(HashMap<String,Object>paramMap){
        String content = HttpClientUtils.getContent("https://open.ys7.com/api/lapp/device/add", paramMap);
        JSONObject jsonObject = JSONObject.parseObject(content);
        return jsonObject;
    }

    @Override
    public JSONObject updateChannel(HashMap<String,Object>paramMap){
        String content = HttpClientUtils.getContent("https://open.ys7.com/api/lapp/camera/name/update", paramMap);
        JSONObject jsonObject = JSONObject.parseObject(content);
        return jsonObject;
    }

    @Override
    public JSONObject capTure(HashMap<String, Object>paramMap){
        String content = HttpClientUtils.getContent("https://open.ys7.com/api/lapp/device/capture", paramMap);
        JSONObject jsonObject = JSONObject.parseObject(content);
        return jsonObject;
    }

    @Override
    public JSONObject deviceInfo(HashMap<String, Object>paramMap){
        String content = HttpClientUtils.getContent("https://open.ys7.com/api/lapp/device/info", paramMap);
        JSONObject jsonObject = JSONObject.parseObject(content);
        return jsonObject;
    }

}
