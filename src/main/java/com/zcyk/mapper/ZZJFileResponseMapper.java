package com.zcyk.mapper;

import com.zcyk.pojo.ZZJFileResponse;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/*功能描述:筑智建返回
*版本信息:Copyright(c)2019
*公司信息:智辰云科
*开发人员:xlyx
*版本日志:1.0
*创建日期:2019/11/21 15:49
*/
@Repository
public interface ZZJFileResponseMapper extends Mapper<ZZJFileResponse> {

    /*根据项目id查询所有的记录*/
    @Select("select * from t_zzjfileresponse where projectid = #{0}")
    List<ZZJFileResponse> selectByProjectId(String projectid);
}
