package com.zcyk.pojo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "project_quality_plan_details")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class ProjectQualityPlanDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
  private String id;

  private String quality_plan_id;

  /*巡检部位*/
  private String check_point;
  /*巡检人员*/
  private String check_user;
  /*巡检时间*/
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern="yyyy-MM-dd")
  private Date check_date;

  /*巡检时间*/
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date create_date;

  /*0删除 1么得问题 2有问题*/
  private Integer status;



}
