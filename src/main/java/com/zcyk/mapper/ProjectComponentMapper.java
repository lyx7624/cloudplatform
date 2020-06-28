package com.zcyk.mapper;

import com.zcyk.pojo.BimVip;
import com.zcyk.pojo.ProjectComponent;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ProjectComponentMapper extends Mapper<ProjectComponent> {



}
