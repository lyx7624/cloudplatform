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
@Accessors(chain = true)
@Table(name = "t_unitproject")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TUnitproject {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
  private String id;

  /*项目名称*/
  private String name;
  /*创建时间*/
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date createtime;
  /*项目状态 0删除 1施工中 2完工*/
  private Integer status;
  /*工程id*/
  private String project_id;
  /*项目编码*/
  private String code;
  /*监理单位*/
  private String supervising_unit;
  /*监理人员*/
  private String supervising_user;
  /*完功时间*/
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date fishwork_date;
  /*项目类型*/
  private String project_type;
  /*项目负责人*/
  private String charge_user;
  /*项目负责人电话*/
  private String charge_phone;
  /*项目负责人岗位*/
  private String user_post;

  /*同步到筑智建状态*/
  @Transient
  private Integer zzjstatus;

  /*同步到筑业云资料状态*/
  @Transient
  private Integer yunstatus;


}
