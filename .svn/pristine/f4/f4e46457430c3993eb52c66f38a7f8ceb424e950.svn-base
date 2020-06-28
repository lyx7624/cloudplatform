package com.zcyk.pojo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "project_quality_inspection")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class ProjectQualityInspection {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
  private String id;
  /*巡检编号*/
  private String inspection_number;
  /*巡检日期  填表时间*/
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern="yyyy-MM-dd")
  private Date inspection_date;
  /*定位*/
  private String toponym;
  /*整改部位*/
  private String position;
  /*填报人*/
  private String informant;
  /*备注*/
  private String remarks;
  /*项目id*/
  private String project_id;
  /*项目id*/
  @Transient
  private String project_name;
  /*质检员*/
  private String quality_inspector;
  /*质检情况*/
  private String quality_situation;
  /*整改措施*/
  private String follow_measures;

  private Integer status;
  /*任务执行人*/
  private String executor;
  /*指派时间  填报时间*/
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern="yyyy-MM-dd")
  private Date appointed_date;
  /*整改开始时间*/
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern="yyyy-MM-dd")
  private Date abarbeitung_date;

  /*分部工程名称*/
  private String sub_project;
  /*分项工程名称*/
  private String item_project;

  /*所对应的的巡检计划*/
  private String quality_plan_details_id;

  @Transient
  List<ProjectQualityInspectionPic> picPaths;





}
