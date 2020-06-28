package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
* 功能描述: 单位工程审核记录
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/10/9 15:17
*/

@Data
@Entity
@Accessors(chain = true)
@Table(name = "t_subrecord")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TSubrecord {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
  private String id;
  /*类型 1地基与基础 2土地结构 3建筑节能 4竣工预验收*/
  private Integer type;
  /*单位工程id*/
  private String unitpriject_id;
  /*审核意见*/
  private String opinion;
  /*审核时间*/
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date audit_time;
  /*状态*/
  private Integer status;

  /*质量验收记录资料数量*/
  @Transient
  private Integer quality_acceptance;

  /*质量控制记录资料数量*/
  @Transient
  private Integer quality_cqontrol;

  /*安全功能....记录资料数量*/
  @Transient
  private Integer safety_function;

  /*观感质量记录资料数量*/
  @Transient
  private Integer quality_appearance;

  /*表格*/
  @Transient
  private List<TSubtable> tSubtables;


}
