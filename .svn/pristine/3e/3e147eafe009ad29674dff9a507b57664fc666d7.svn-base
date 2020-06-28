package com.zcyk.mapper;

import com.zcyk.pojo.Project;
import com.zcyk.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 项目管理接口
 */
@Repository
public interface ProjectModelMapper extends Mapper<Project> {


    /*根据项目名称编码和企业id查询项目*/
    @Select("select * from t_projectmodel where project_status != 0 and project_name = #{project_name} and company_id = #{company_id}")
    Project selectProjectName(Project project);

    /*根据项目名称编码和企业id查询项目*/
    @Select("select * from t_projectmodel where project_status != 0 and project_code = #{project_code} and company_id = #{company_id}")
    Project selectProjectCode(Project project);

    /*查看公司项目（个人）*/
    @Select("select * from t_projectmodel p " +
            " join user_project u on u.project_id = p.id " +
            " where u.user_id = #{user.user_id} and p.company_id = #{user.company_id} and project_status ！= 0" +
            " and (p.project_name like '${search}' or p.construction_unit like '${search}' or p.supervising_user like '${search}' )")
    List<Project> selectProject(@Param("search") String search, @Param("user")User user);

    /*查看公司项目（全部）*/
    @Select("select * from t_projectmodel p " +
            " where p.company_id = #{user.company_id} and project_status != 0" +
            " and (p.project_name like '%${search}%' or p.supervising_unit like '%${search}%' or p.construction_unit like '%${search}%' or p.supervising_user like '%${search}%' or p.project_code like '%${search}%' or p.map_coordinates = #{search}) order by startwork_date desc")
    List<Project> selectAllProject(@Param("search") String search, @Param("user")User user);

    @Select("select * from t_projectmodel where company_id = #{0} and project_status != 0")
    List<Project> getAllProject(String company_id);

    /*根据时间范围查询项目*/
    @Select("select * from t_projectmodel where company_id = #{company_id} and project_status!=0 and startwork_date between #{startTime} and #{endTime}")
    List<Project> selectProjectByTime(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("company_id") String company_id);

    /*删除工程（逻辑）*/
    @Update("update t_projectmodel set project_status = 0 where id = #{0}")
    Integer deleteProject(String id);

    /*将预计竣工日期设置为空*/
    @Update("update t_projectmodel set plannedend_date = null where id = #{0}")
    void updatePlannedend_dateToNull(String id);

    /*将预计开始时间设置为空*/
    @Update("update t_projectmodel set plannedstart_date = null where id = #{0}")
    void updatePlannedstart_dateToNull(String id);

    /*查询公司项目(时间降序)*/
    @Select("select p.*,b.status ZZJStatus from t_projectmodel p inner join bim_vip b on p.id = b.account_id where p.company_id = #{company_id} and p.project_status != 0 and p.is_bim = 1 and p.project_name like '%${search}%' order by p.project_createtime desc")
    List<Project> selectBimProjectByTimeD(@Param("company_id") String company_id, @Param("search") String search);

    /*查询公司项目(时间升序)*/
    @Select("select p.*,b.status ZZJStatus from t_projectmodel p inner join bim_vip b on p.id = b.account_id where p.company_id = #{company_id} and p.project_status != 0 and p.is_bim = 1 and p.project_name like '%${search}%' order by p.project_createtime asc")
    List<Project> selectBimProjectByTimeA(@Param("company_id") String company_id, @Param("search") String search);

    /*查询公司项目(进度升序)*/
    @Select("select p.*,b.status ZZJStatus from t_projectmodel p inner join bim_vip b on p.id = b.account_id where p.company_id = #{company_id} and p.project_status != 0 and p.is_bim = 1 and p.project_name like '%${search}%' order by p.works asc")
    List<Project> selectBimProjectByWorksA(@Param("company_id") String company_id, @Param("search") String search);

    /*查询公司项目(进度降序)*/
    @Select("select p.*,b.status ZZJStatus from t_projectmodel p inner join bim_vip b on p.id = b.account_id where p.company_id = #{company_id} and p.project_status != 0 and p.is_bim = 1 and p.project_name like '%${search}%' order by p.works desc")
    List<Project> selectBimProjectByWorksD(@Param("company_id") String company_id, @Param("search") String search);

    /*获取Bim项目模型信息*/
    @Select("select * from t_projectmodel where id = #{id} and status != 0")
    Project getProject(@Param("id") String id);

    /*更改项目类型状态*/
    @Update("update t_projectmodel set is_bim = 0 where id = #{id}")
    int cancelBimProject(@Param("id") String id);

    /*更改项目类型状态*/
    @Update("update t_projectmodel set yunfile_account = #{yunfile_account} where id = #{project_id}")
    void addProjectToYun(@Param("project_id")String project_id,@Param("yunfile_account") String yunfile_account);
    /*查询没有项目激活码的项目*/
    @Select("select p.id from t_projectmodel p where p.company_id = #{company_id} and p.project_status != 0 and p.is_bim = 1 " +
            " and not exists (select b.account_id from bim_vip b where p.id = b.account_id )")
    List<String> selectNoBimCodeProject(String company_id);

    @Select("select * from t_projectmodel where project_status != 0 and id in (select project_id from user_project_role where user_phone = #{0} and status = 1)")
    List<Project> getUserAllProject(String user_account);
}
