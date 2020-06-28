package com.zcyk.pojo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

/**
* 功能描述:流程节点表
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/8/11 9:02
*/
@Data
@Entity
@Table(name = "process_node")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessNode {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
  private String id;

  /*节点名称*/
  private String node_name;
  /*流程id*/
  private String process_id;
  /*节点排序*/
  private Integer node_order;
  /*备注*/
  private String remark;
  /*处理人姓名*/
  private String handler_name;
  /*处理人id*/
  private String handler_id;
  /*是否盖章*/
  private Integer isstamp;
  /*节点 0已删除 1待处理 2正在处理 3完成 4退回 */
  private Integer status;
  /*处理时间*/
  private String handler_time;


  @Transient
  private List<T_Opinion> opinions;



}
