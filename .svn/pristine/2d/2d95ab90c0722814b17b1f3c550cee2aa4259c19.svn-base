package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
* 功能描述: 文档表格
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/10/9 15:17
*/

@Data
@Entity
@Accessors(chain = true)
@Table(name = "t_subtable")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TSubtable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;
    /*分布工程记录id*/
    private String subrecord_id;
    /*状态*/
    private Integer status;
    /*表号*/
    private String table_name;
    /*资料类型 1质量验收记录 2核查记录 3安全。。。。 4观感质量验收记录*/
    private Integer data_type;





}
