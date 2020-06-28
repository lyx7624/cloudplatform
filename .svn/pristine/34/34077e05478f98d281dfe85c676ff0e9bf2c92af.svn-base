package com.zcyk.mapper;

import com.zcyk.pojo.File;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface FileMapper extends Mapper<File> {

    /*根据文件夹id查询所有文件*/
    @Select("select * from t_file where folder_id = #{0} and file_statu = 1 order by file_createtime DESC")
    List<File> selectFileByFolderId(String folder_id);

    /*修改文件名*/
    @Update("update t_file set file_name = #{file_name} where id = #{id} and folder_id = #{folder_id}")
    Integer updateFileName(File file);

    /*删除d单个文件*/
    @Update("update t_file set file_statu = 0 where id = #{id} and folder_id = #{folder_id}")
    Integer updateFileStatus(String id, String folder_id);

    /*删除文件夹下文件*/
    @Update("update t_file set file_statu = 0 where folder_id = #{folder_id}")
    Integer deleteFile(String folder_id);

    /*判断是否是文件*/
    @Select("select id from t_file where id=#{id} and folder_id = #{folder_id}")
    String judgeFile(String id, String folder_id);

    /*根据文件夹id查询所有文件*/
    @Select("select * from t_file where folder_id = #{folder_id} and file_statu = 1 and (file_name like '%${index}%' or file_type like '%${index}%' or file_createuser like '%${index}%')")
    List<File> indexfile(String folder_id, String index);

    /*根据上传者查询所有文件*/
    @Select("select * from t_file where file_createuser_id = #{file_createuser_id} and file_statu = 1")
    List<File> Allfile(String file_createuser_id);



    /*根据Id改文件名*/
    @Update("update t_file set file_name = #{file_name} where id = #{id}")
    int updateFileNameById(String id, String file_name);

    /*检查重名*/
    @Select("select * from t_file where folder_id = #{folder_id} and file_name = #{file_name} and file_statu = 1")
    File selectFileName(File file);
    /*根据url找文件*/
    @Select("select * from t_file where file_url = #{0} and file_statu = 1")
    File selectByUrl(String url);
}
