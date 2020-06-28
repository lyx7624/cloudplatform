package com.zcyk.pojo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
* 功能描述:模板表
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/8/11 9:03
*/
@Data
@Entity
@Table(name = "t_template")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class  T_Template {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
  private String id;
  //模板名称
  private String template_name;
  //模板创建时间
  private String create_time;
  //创建人
  private String create_user;
  //模板状态
  private Integer template_status;

  private String company_id;


}
