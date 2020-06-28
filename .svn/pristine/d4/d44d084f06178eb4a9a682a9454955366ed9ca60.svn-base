package com.zcyk.mapper;

import com.zcyk.pojo.Model_cost;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ModelCostMapper extends Mapper<Model_cost> {
    @Select("select * from model_cost where mid = #{mid} and statu = 1 and component_name like '%${search}%' order by create_time desc")
    List<Model_cost> selectModelCost(@Param("mid") String mid, @Param("search") String search);

    @Select("select total_prices from model_cost where mid = #{mid} and statu = 1")
    List<BigDecimal> getTotalPrices(String mid);
}
