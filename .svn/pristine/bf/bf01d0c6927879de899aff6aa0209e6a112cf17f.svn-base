package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
* 功能描述: 流程实例表
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/8/11 9:02
*/
@Data
@Entity
@Table(name = "process_info")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessInfo {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
  private String id;

  //标题
  private String title;

  //完成时间
  private String fish_time;

  //创建时间
  private String create_time;

  //模板id
  private String template_id;

  //模板名称
  private String template_name;

  //发起人id
  private String initiator_id;

  //发起人姓名
  private String initiator_name;

  //处理人
  private String hanlder_id;

  //处理人
  private String hanlder_name;

  //备注
  private String remark;

  //流程进度
  private String template_node_name;

  //流程文件word
  private String process_files_word;

  //流程文件pdf
  private String process_files_pdf;

  private String file_name;

  private String company_id;


  /*流程状态 0 删除 1未删除未完成 2已完成 3非正常完成 */
  private Integer process_status;




}
