package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 功能描述:设计变更记录表
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/12/31 9:07
*/
@Data
@Entity
@Table(name = "project_design_alteration")
@Accessors(chain = true)
public class ProjectDesignAlteration {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
  private String id;
  private String project_id;
  @Transient
  private String project_name;
  /*记录人*/
  private String recorder;
  /*审核人*/
  private String verifier;
  /*记录时间*/
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern="yyyy-MM-dd")
  private String write_date;
  /*报告编号*/
  private String report_number;
  /*收图日期*/
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern="yyyy-MM-dd")
  private String drawing_date;
  /*专业、图名、版本*/
  private String tags;
  /*重要性ABC*/
  private String significance;
  /*问题描述*/
  private String problem_description;
  /*标高*/
  private String elevation;
  /*轴号*/
  private String axis_number;
  /*专业类别*/
  private String special_type;

  /*专项名称*/
  private String special_name;

  /*状态 0 删除 1未解决 2已解决*/
  private Integer status;

  @Transient
  private Integer numOrder;

//
  @Transient
  List<ProjectDesignAlterationOpinion> opinions;
  @Transient
  List<ProjectDesignAlterationPic> picPaths;

  public Map<String,Object> toMap(){
    Map<String,Object> map= new HashMap<>();
    for(Field field : ProjectDesignAlteration.class.getDeclaredFields()){
      try{
        map.put(field.getName(), field.get(this));
      }catch(Exception e){

      }

    }
    return map;
  }


}
