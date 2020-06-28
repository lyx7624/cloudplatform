package com.zcyk.mapper;

import com.zcyk.pojo.BimVip;
import com.zcyk.pojo.YunfileVip;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface YunfileVipMapper extends Mapper<YunfileVip> {


    /*1就是会员*/
    @Select("select * from yunfile_vip where company_id = #{company_id} ")
    List<YunfileVip> getByCompany(@Param("company_id") String company_id);

    /*找到会员*/
    @Select("select * from yunfile_vip where  account = #{account} and status = 1 ")
    YunfileVip selectByAccount(@Param("account") String account);

    /*找到会员*/
    @Update("update yunfile_vip set status=#{status} where account = #{account} ")
    void setStatus(@Param("account") String account,@Param("status") Integer status);
}
