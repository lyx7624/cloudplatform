package com.zcyk.mapper;

import com.zcyk.pojo.ProjectFolder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ProjectFolderMapper extends Mapper<ProjectFolder> {
    /*根据父ID查子文件夹*/
    @Select("select * from project_folder  where parent_id = #{0} and folder_statu=1 order by folder_createtime DESC")
    List<ProjectFolder> selectChildFolderById(String folder_id);

    /*查wen件夹*/
    @Select("select * from project_folder  where id = #{0} and folder_statu=1")
    ProjectFolder selectFolderById(String folder_id);

    /*删除文件夹*/
    @Update("update project_folder set folder_statu=0 where id = #{0}")
    Integer updateProjectfolderStatu(String folder_id);

    /*搜索项目文件夹*/
    @Select("select * from project_folder where parent_id = #{folder_id} and folder_statu=1 and (folder_name like '%${index}%' or folder_createuser like '%${index}%')")
    List<ProjectFolder> searchProjectFolder(@Param("index") String index, @Param("folder_id") String folder_id);

    /*通过父id查找字文件夹*/
    @Select("select * from project_folder where parent_id = #{folder_id} and folder_statu =1")
    List<ProjectFolder> selectProjectFolderByParentId(String folder_id);

    /*根据父ID和文件名参照文件*/
    @Select("select * from project_folder where folder_statu = 1 and folder_name = #{folder_name} and parent_id = #{parent_id}")
    ProjectFolder selectFolderName(String folder_name, String parent_id);



    @Update("update project_folder set folder_name=#{folder_name},folder_updateuser = #{folder_updateuser},folder_updatetime = #{folder_updatetime}" +
            " where parent_id = #{parent_id}")
    int updateFolderName(ProjectFolder projectFolder);


    @Update("update project_folder set folder_name=#{folder_name},set folder_updateuser = #{folder_updateuser},set folder_updatetime = #{folder_updatetime}" +
            " where parent_id = #{parent_id}")
    int updateParentFolderName(ProjectFolder projectFolder);

    /*根据用户电话号码获取项目顶级文件夹
    * 项目已删除（用户角色状态为0） 也能查到顶级文件夹
    * */
    @Select("select  * from project_folder pf inner join user_project_role upr on pf.project_id = upr.project_id " +
            " where pf.folder_statu = 1 and upr.user_phone = #{0} and pf.project_id = pf.parent_id and upr.status != 0 group by pf.id")
    List<ProjectFolder> selectFolderByUser(String user_account);
}
