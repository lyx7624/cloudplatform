package com.zcyk.mapper;


import com.zcyk.pojo.Company;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Repository
public interface CompanyMapper extends Mapper<Company> {
   @Select("select * from t_company where company_name = #{company_name} and company_status = 1")
   Company checkCompanyName(String company_name);

   @Select("select company_name from t_company where company_status = 1")
   List<String> getAllCompany();

    @Select("select * from t_company where company_status = 1 and id = #{0}")
    Company selectById(String company_id);
}
