package com.zcyk.mapper;

import com.zcyk.pojo.BimVip;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface BimVipMapper extends Mapper<BimVip> {


    /*根据项目查询激活码 和状态*/
    @Select("select status,activation_code from bim_vip where account_id = #{0}")
    BimVip selectOneVip(String project_id);

    @Update("update bim_vip set status = 1 where account_id = #{account_id}")
    int setVip(@Param("account_id") String account_id);

    @Select("select * from bim_vip  where status = 1 and account_id in (select id from t_projectmodel where company_id = #{0} and project_status != 0)")
    List<BimVip> selectCompanyVipBims(String company_id);
}
