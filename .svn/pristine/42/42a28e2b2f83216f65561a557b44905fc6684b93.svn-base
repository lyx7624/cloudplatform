package com.zcyk.service;

import com.zcyk.pojo.Model;
import com.zcyk.dto.ZZJ_Token;
import com.zcyk.pojo.ZZJFileResponse;
import com.zcyk.dto.ZZJItem;

/**
 * 功能描述: 筑智建相关接口
 * 开发人员: xlyx
 * 创建日期: 2019/11/19 10:54
 */
public interface ZZJFileService {

    /**
     * 功能描述：用户登录
     * 开发人员： lyx
     * 创建时间： 2019/11/19 11:24
     * 参数：
     * 返回值：
     * 异常：
     */
     ZZJ_Token loginZZJ(String user_account, String user_password) throws Exception;
    /**
     * 功能描述：刷新token
     * 开发人员： lyx
     * 创建时间： 2019/11/19 11:24
     * 参数：
     * 返回值：
     * 异常：
     */
     ZZJ_Token refreshToken(String user_account, String refresh_token, String authorization) throws Exception;


    /**
     * 功能描述：上传文件  没有判断是否是rvt文件类型
     * 开发人员： lyx
     * 创建时间： 2019/11/19 11:24
     * 参数：
     * 返回值：
     * 异常：
     */
     ZZJFileResponse upFile(Model model, String authorization) throws Exception;
     ZZJFileResponse upFileSync(Model model, String authorization) throws Exception;


    /**
     * 功能描述：转换项目组文件  未知其意
     * 开发人员： lyx
     * 创建时间： 2019/11/19 11:24
     * 参数：
     * 返回值：
     * 异常：
     */
     String transformFile(ZZJFileResponse zzjFileResponse , String authorization) throws Exception ;

    /**
     * 功能描述：查看项目组转换状态  未知其意
     * 开发人员： lyx
     * 创建时间： 2019/11/19 11:24
     * 参数：
     * 返回值：
     * 异常：
     */
     String transformStatus(String modelGroupId,String taskId ,String authorization) throws Exception ;


    ZZJItem getFileStatus(String hash, Integer totalChunks, String authorization) throws Exception;


     /**
      * 功能描述：同步文件接口
      * 开发人员： lyx
      * 创建时间： 2019/11/23 10:37
      * 参数：
      * 返回值：
      * 异常：
      */
    Integer sycFile(String hash,String authorization) throws Exception;

    void deleteFile(java.io.File file) throws Exception;

}