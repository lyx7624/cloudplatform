package com.zcyk.mapper;


import com.zcyk.pojo.UserFolder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface UserfolderMapper extends Mapper<UserFolder> {

    @Select("select * from user_folder  where parent_id = #{0} and folder_statu=1 order by folder_createtime DESC")
    List<UserFolder> selectUserfolderByparent_Id(String parent_id);

    @Select("select * from user_folder  where user_id = #{0} and folder_statu=1")
    List<UserFolder> selectUserfolderByUser_Id(String user_id);

    /*删除文件夹*/
    @Update("update user_folder set folder_statu = 0 where id = #{0}")
    int updateUserfolderStatu(String id);

    @Select("select * from user_folder where  parent_id = #{folder_id} and folder_statu = 1 and folder_name like '%${index}%'")
    List<UserFolder> indexUserfolder(@Param("index") String index,@Param("folder_id") String folder_id);

    /*检查重名*/
    @Select("select * from user_folder where folder_statu = 1 and folder_name = #{folder_name} and parent_id = #{parent_id}")
    UserFolder selectFolderName(String folder_name, String parent_id);

    @Select("select * from user_folder where  parent_id = #{parent_id} and folder_statu = 1")
    UserFolder selectTopFolder(@Param("parent_id") String parent_id);




}
