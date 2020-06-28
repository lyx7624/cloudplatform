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
@Table(name = "project_quality_plan")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class ProjectQualityPlan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
  private String id;
  private String plan_name;
  private String unitproject_id;

  private String unitproject_name;

  private String project_name;
  private String project_id;
  /*建设单位*/
  private String construction_unit;
  /*施工负责人*/
  private String construction_user;

  /*创建人*/
  private String create_user;

  /*创建时间*/
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date create_date;

  private Integer status;

  @Transient
  List<ProjectQualityPlanDetails> planDetails;



}
