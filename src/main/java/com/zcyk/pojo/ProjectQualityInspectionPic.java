package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
* 功能描述: 质量巡检图片
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2020/1/2 10:38
*/
@Data
@Entity
@Table(name = "project_quality_inspection_pic")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class ProjectQualityInspectionPic {

  @Id
  private Integer id;
  /*巡检id*/
  private String project_quality_inspection_id;
  private String pic_url;
  private String pic_name;
  private String status;



}
