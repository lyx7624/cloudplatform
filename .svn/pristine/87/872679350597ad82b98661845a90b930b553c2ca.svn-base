package com.zcyk.mapper;

import com.zcyk.pojo.CompanyPublicfolder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface CompanyfolderMapper extends Mapper<CompanyPublicfolder>,YunFileMapper<CompanyPublicfolder> {
    /*查找顶级文件夹*/
    @Select("select * from company_publicfolder where parent_id=#{0} and folder_statu=1")
    CompanyPublicfolder selectCompanyPublicfolderByCompanyId(String companyid);

    /*查找文件的子文件夹*/
    @Select("select * from company_publicfolder where parent_id=#{0} and folder_statu=1 order by folder_createtime DESC")
    List<CompanyPublicfolder> selectCompanyfolderByParentId(String folder_id);

    @Update("update company_publicfolder set folder_statu = 0 where id = #{0}")
    int updateCompanyfolderStatu(String id);

    @Select("select * from company_publicfolder where parent_id = #{folder_id} and folder_statu=1 and (folder_name like '%${index}%' or folder_createuser like '%${index}%')")
    List<CompanyPublicfolder> searchCompanyFolder(@Param("index") String index, @Param("folder_id") String folder_id);

    //    @Select("select * form company_publicfolder where id = #{0} ")
//    List<CompanyPublicfolder> selectCompanyfolderById(String id);
//}
    @Select("select * from company_publicfolder where folder_statu=1 and folder_name = #{folder_name} and parent_id = #{parent_id}")
    CompanyPublicfolder selectFolderName(String folder_name, String parent_id);
}
