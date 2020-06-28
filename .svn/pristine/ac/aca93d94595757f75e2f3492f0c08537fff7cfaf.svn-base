package com.zcyk.service;

import com.github.pagehelper.PageInfo;
import com.zcyk.pojo.ProjectDesignAlteration;
import com.zcyk.pojo.ProjectDesignAlterationOpinion;
import com.zcyk.pojo.ProjectDesignAlterationPic;

import java.util.List;
import java.util.Map;

/**
* 功能描述:项目设计变更
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/12/31 9:13
*/
public interface ProjectDesignService {

    /**
     * 功能描述：修改、新增记录
     * 开发人员： lyx
     * 创建时间： 2019/12/31 9:58
     * 参数：
     * 返回值：
     * 异常：
     */
    String updateDesignAlteration(ProjectDesignAlteration projecDesignAlteration) throws Exception;

    /**
     * 功能描述：记录绑定图片
     * 开发人员： lyx
     * 创建时间： 2019/12/31 9:58
     * 参数：
     * 返回值：
     * 异常：
     */
    void updateDesignAlterationPic(List<ProjectDesignAlterationPic> picPaths, String designAlterationId) throws Exception;
    /**
     * 功能描述：记录意见新增或者修改
     * 开发人员： lyx
     * 创建时间： 2019/12/31 9:58
     * 参数：
     * 返回值：
     * 异常：
     */
    void updateDesignAlterationOpinions(List<ProjectDesignAlterationOpinion> opinions, String designAlterationId) throws Exception;


    /**
     * 功能描述：删除记录
     * 开发人员： lyx
     * 创建时间： 2019/12/31 10:22
     * 参数：
     * 返回值：
     * 异常：
     */
    void deleteDesignAlteration(String id) throws Exception;

    /**
     * 功能描述：查询-专业类型
     * 开发人员： lyx
     * 创建时间： 2019/12/31 10:22
     * 参数：
     * 返回值：
     * 异常：
     */
    Map<String,Object> selectDesignAlteration(int pageNum, int pageSize, String search, String project_id) throws Exception;

    /**
     * 功能描述：查询-重要程度
     * 开发人员： lyx
     * 创建时间： 2019/12/31 10:22
     * 参数：
     * 返回值：
     * 异常：
     */
    PageInfo<ProjectDesignAlteration> selectDesignAlteration(int pageNum,int pageSize,String search, String special_name, String project_id, String significance) throws Exception;


    /**
     * 功能描述：查询-变更记录、意见、图片
     * 开发人员： lyx
     * 创建时间： 2019/12/31 10:22
     * 参数：
     * 返回值：
     * 异常：
     */
    ProjectDesignAlteration selectDesignAlterationById(String id) throws Exception;

    /**
     * 功能描述：查询-根据项目id
     * 开发人员： lyx
     * 创建时间： 2019/12/31 10:22
     * 参数：
     * 返回值：
     * 异常：
     */
   List<ProjectDesignAlteration> selectDesignAlterationByProjectId(String id) throws Exception;


   /**
    * 功能描述：查询项目设计变更的图片
    * 开发人员： lyx
    * 创建时间： 2020/1/2 9:19
    * 参数：
    * 返回值：
    * 异常：
    */
    List<ProjectDesignAlterationPic> selectDesignAlterationPic(String id) throws Exception;

    /**
     * 功能描述：查询项目设计变更的意见
     * 开发人员： lyx
     * 创建时间： 2020/1/2 9:19
     * 参数：
     * 返回值：
     * 异常：
     */
    List<ProjectDesignAlterationOpinion> selectDesignAlterationOpinions(String id) throws Exception;

    /**
     * 功能描述：提交回复
     * 开发人员： lyx
     * 创建时间： 2020/1/2 9:19
     * 参数：
     * 返回值：
     * 异常：
     */
    void addDesignReply(ProjectDesignAlterationOpinion opinion)throws Exception;
}
