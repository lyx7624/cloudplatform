package com.zcyk.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zcyk.mapper.*;
import com.zcyk.pojo.*;
import com.zcyk.service.ProjectDesignService;
import com.zcyk.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能描述:项目设计变更
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/12/31 9:13
 */
@Service
@Transactional
public class ProjectDesignServiceImpl implements ProjectDesignService {


    @Autowired
    ProjectDesignMapper projectDesignMapper;

    @Autowired
    ProjectDynamicMapper projectDynamicMapper;

    @Autowired
    ProjectDesignOpinionMapper projectDesignOpinionMapper;

    @Autowired
    ProjectDesignPicMapper projectDesignPicMapper;

    @Autowired
    HttpServletRequest request;

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserService userService;



    /**
     * 功能描述：查询-变更记录、意见、图片
     * 开发人员： lyx
     * 创建时间： 2019/12/31 10:22
     * 参数：
     * 返回值：
     * 异常：
     */
    public ProjectDesignAlteration selectDesignAlterationById(@NotNull String id) {
        Map<String, Object> map = new HashMap<>();
        ProjectDesignAlteration projectDesignAlteration = projectDesignMapper.selectById(id);
        if(projectDesignAlteration!=null){
            List<ProjectDesignAlterationPic> projectDesignAlterationPics = projectDesignPicMapper.selectByProjectDesignId(id);
            List<ProjectDesignAlterationOpinion> projectDesignAlterationOpinions = projectDesignOpinionMapper.selectByProjectDesignId(id);
            projectDesignAlteration.setOpinions(projectDesignAlterationOpinions).setPicPaths(projectDesignAlterationPics);
        }
        return projectDesignAlteration;
    }

    /**
     * 功能描述：查询-根据项目id
     * 开发人员： lyx
     * 创建时间： 2019/12/31 10:22
     * 参数：
     * 返回值：
     * 异常：
     */
    public List<ProjectDesignAlteration> selectDesignAlterationByProjectId(String id) {
        return projectDesignMapper.selectDesignAlterationByProjectId(id);
    }

    /**
     * 功能描述：修改、新增记录
     * 开发人员： lyx
     * 创建时间： 2019/12/31 9:58
     * 参数：
     * 返回值：
     * 异常：
     */
    public String updateDesignAlteration(ProjectDesignAlteration projecDesignAlteration){
        String company_id = userService.getNowUser(request).getCompany_id();
        if(StringUtils.isNotBlank(projecDesignAlteration.getId())){//修改
            projectDesignMapper.updateByPrimaryKeySelective(projecDesignAlteration);
        }else {
            //新增  时间都是前端传过来 前端做得校验
            projecDesignAlteration.setId(UUID.randomUUID().toString()).setStatus(1);
            projectDesignMapper.insertSelective(projecDesignAlteration);
        }

        return projecDesignAlteration.getId();

    }

    /**
     * 功能描述：记录绑定图片
     * 开发人员： lyx
     * 创建时间： 2019/12/31 9:58
     * 参数：
     * 返回值：
     * 异常：
     */
    public void updateDesignAlterationPic(List<ProjectDesignAlterationPic> picPaths, String designAlterationId){

        /*物理删除，因为这样简单粗暴*/
        projectDesignPicMapper.deleteByDesignAlterationId(designAlterationId);
        ProjectDesignAlterationPic picR = new ProjectDesignAlterationPic();
        picR.setProject_design_alteration_id(designAlterationId);
        for (int i = 0; i <picPaths.size() ; i++) {
            projectDesignPicMapper.insertSelective(picR.setPic_url(picPaths.get(i).getPic_url()).setPic_name(picPaths.get(i).getPic_name()));
        }



    }

    /**
     * 功能描述：记录绑定意见
     * 开发人员： lyx
     * 创建时间： 2019/12/31 9:58
     * 参数：
     * 返回值：
     * 异常：
     */
    public void updateDesignAlterationOpinions(List<ProjectDesignAlterationOpinion> opinions, String designAlterationId) {
        /*物理删除简单除暴*/
        projectDesignOpinionMapper.deleteByDesignAlterationId(designAlterationId);
        opinions.forEach(o->{
            o.setProject_design_alteration_id(designAlterationId);
            projectDesignOpinionMapper.insertSelective(o);
            if(o.getStatus()==3){
                projectDesignMapper.updateStatus(designAlterationId,2);
            }
        });
    }

    /**
     * 功能描述：删除记录
     * 开发人员： lyx
     * 创建时间： 2019/12/31 10:22
     * 参数：
     * 返回值：
     * 异常：
     */
    public void deleteDesignAlteration(String id){
        projectDesignMapper.updateStatus(id,0);
        //删除这个设计记录的动态
        projectDynamicMapper.updateStatus(id,0);

    }



    /**
     * 功能描述：查询-分类型显示
     * 开发人员： lyx
     * 创建时间： 2019/12/31 10:22
     * 参数：
     * 返回值：
     * 异常：
     */
    public Map<String,Object> selectDesignAlteration(int pageNum,int pageSize,String search, String project_id){
        Map<String,Object> map = new HashMap<>();
        List<Map<String,Object>> listMap = new ArrayList<>();
        int count = projectDesignMapper.selectGroupSpecialName(search,project_id).size();
        PageHelper.startPage(pageNum,pageSize);
        List<ProjectDesignAlteration> projectDesignAlterations = projectDesignMapper.selectGroupSpecialName(search,project_id);
        projectDesignAlterations.forEach(p->{//十个专项进行数据查找
            Map<String,Object> one = new HashMap<>();
            List<ProjectDesignAlteration> projectDesignAlteration = projectDesignMapper.selectBySpecialName(project_id, p.getSpecial_name());
            List<ProjectDesignAlteration> SpecialNameA = projectDesignAlteration.stream().filter(p1 -> p1.getSignificance().equals("A")).collect(Collectors.toList());
            List<ProjectDesignAlteration> SpecialNameB = projectDesignAlteration.stream().filter(p1 -> p1.getSignificance().equals("B")).collect(Collectors.toList());
            List<ProjectDesignAlteration> SpecialNameC = projectDesignAlteration.stream().filter(p1 -> p1.getSignificance().equals("C")).collect(Collectors.toList());
            one.put("A",SpecialNameA.size());
            one.put("B",SpecialNameB.size());
            one.put("C",SpecialNameC.size());
            one.put("count",projectDesignAlteration.size());
            one.put("special_name",p.getSpecial_name());
            listMap.add(one);
        });
        map.put("list",listMap);
        map.put("total",count);
        map.put("pageNum",pageNum);
        return map;
    }

    /**
     * 功能描述：查询-重要程度
     * 开发人员： lyx
     * 创建时间： 2019/12/31 10:22
     * 参数：
     * 返回值：
     * 异常：
     */
    public PageInfo<ProjectDesignAlteration> selectDesignAlteration(int pageNum, int pageSize, String search, String special_name, String project_id, String significance) {
        PageHelper.startPage(pageNum,pageSize);
        List<ProjectDesignAlteration> projectDesignAlterations = projectDesignMapper.selectBySpecialAndSignificance(search,special_name,project_id,significance);
        PageInfo<ProjectDesignAlteration> pageInfo = new PageInfo<>(projectDesignAlterations);
        return pageInfo;
    }


    /**
     * 功能描述：查询项目设计变更的图片
     * 开发人员： lyx
     * 创建时间： 2020/1/2 9:19
     * 参数：
     * 返回值：
     * 异常：
     */
    public List<ProjectDesignAlterationPic> selectDesignAlterationPic(String id) {
        return projectDesignPicMapper.selectByProjectDesignId(id);
    }

    /**
     * 功能描述：查询项目设计变更的意见
     * 开发人员： lyx
     * 创建时间： 2020/1/2 9:19
     * 参数：
     * 返回值：
     * 异常：
     */
    public List<ProjectDesignAlterationOpinion> selectDesignAlterationOpinions(String id) {
        return projectDesignOpinionMapper.selectByProjectDesignId(id);
    }

    /**
     * 功能描述：提交回复
     * 开发人员： lyx
     * 创建时间： 2020/1/2 9:19
     * 参数：
     * 返回值：
     * 异常：
     */
    public void addDesignReply(ProjectDesignAlterationOpinion opinion) {
        projectDesignOpinionMapper.insert(opinion);
        if(3==opinion.getStatus()){//将这个设计设置为已处理
            projectDesignMapper.updateStatus(opinion.getProject_design_alteration_id(),2);
        }
        //提交回复必有动态 只需修改
        String alterationId = opinion.getProject_design_alteration_id();
        ProjectDesignAlteration projectDesignAlteration = projectDesignMapper.selectById(alterationId);





    }
}
