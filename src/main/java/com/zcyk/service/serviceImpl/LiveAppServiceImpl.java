package com.zcyk.service.serviceImpl;

import com.zcyk.dto.ResultData;
import com.zcyk.mapper.LiveAppMapper;
import com.zcyk.pojo.LiveApp;
import com.zcyk.service.LiveAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author WuJieFeng
 * @date 2020/4/14 13:59
 */
@Service
@Transactional
public class LiveAppServiceImpl implements LiveAppService {
    @Autowired
    LiveAppMapper liveAppMapper;

    /**
     * 功能描述：添加账号应用
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/14 14:11
     * 参数：[ * @param null]
     * 返回值：
    */
    public ResultData addAPP(LiveApp liveApp){


        liveApp.setId(UUID.randomUUID().toString())
               .setStatus(1);
        int i = liveAppMapper.insert(liveApp);
        if(i!=0){
            return new ResultData().setStatus("200").setMsg("绑定成功").setData(liveApp);
        }
        return new ResultData().setStatus("400").setMsg("绑定失败");
    }

    /**
     * 功能描述：保存Token
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/14 14:36
     * 参数：[ * @param null]
     * 返回值：
    */
    public ResultData updateToken(LiveApp liveApp){
        int i = liveAppMapper.updateByPrimaryKeySelective(liveApp);
        if(i!=0){
            return new ResultData().setStatus("200").setMsg("成功").setData(liveApp);
        }
        return new ResultData().setStatus("400").setMsg("失败");
    }

    /**
     * 功能描述：根据id查应用
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/14 14:51
     * 参数：[ * @param null]
     * 返回值：
    */
    public LiveApp selectAppById(String id){
        LiveApp liveApp = liveAppMapper.selectByPrimaryKey(id);
        return liveApp;
    }

    /**
     * 功能描述：根据project查应用
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/14 14:58
     * 参数：[ * @param null]
     * 返回值：
    */
    public List<LiveApp> selectAppByProjectId(String project_id){
       return liveAppMapper.selectDeviceByProjectId(project_id);
    }

    public List<LiveApp> selectDHAppByProjectId(String project_id){
        return liveAppMapper.selectDHDeviceByProjectId(project_id);
    }

    /**
     * 功能描述：删除应用
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/21 16:11
     * 参数：[ * @param null]
     * 返回值：
    */
    public ResultData deleteAppById(String id){
        int i = liveAppMapper.updateByPrimaryKeySelective(new LiveApp().setId(id).setStatus(0));
        if(i==0){
            return new ResultData().setStatus("400").setMsg("删除失败");
        }
        return new ResultData().setStatus("200").setMsg("删除成功");
    }

    /**
     * 功能描述：更改应用名称
     * 开发人员：Wujiefeng
     * 创建时间：2020/4/21 16:18
     * 参数：[ * @param null]
     * 返回值：
    */
    public ResultData updateAppName(String id,String app_name){
        int i = liveAppMapper.updateByPrimaryKeySelective(new LiveApp().setId(id).setApp_name(app_name));
        if(i==0){
            return new ResultData().setStatus("400").setMsg("修改失败");
        }
        return new ResultData().setStatus("200").setMsg("修改成功");
    }
}
