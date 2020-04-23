package com.zcyk.mapper;

import com.zcyk.pojo.UploadFile;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface UploadFileMapper extends Mapper<UploadFile> {

    @Select("select * from upload_file where file_md5 = #{md5}")
    UploadFile findFileByMd5(String md5);
}
