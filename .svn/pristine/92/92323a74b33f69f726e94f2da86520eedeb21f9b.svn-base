package com.zcyk.pojo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * 功能描述:模板节点表
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/8/11 9:02
 */
@Data
@Entity
@Table(name = "template_node")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateNode {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;
    //节点名称
    private String node_name;
    //模板id
    private String template_id;
    //节点顺序
    private Integer node_order;
    /*节点 0未用 1待处理 2正在处理 3完成 4退回 */
    private Integer status;
    //备注
    private String remark;
    //处理人姓名
    private String handler_name;
    //处理人id
    private String handler_id;
    //是否需要盖章
    private Integer isstamp;


}
