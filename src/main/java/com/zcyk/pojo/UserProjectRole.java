package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author WuJieFeng
 * @date 2019/10/9 16:43
 */
@Data
@Entity
@Table(name = "user_project_role")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProjectRole {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;
    /*成员姓名*/
    private String user_name;
    /*成员id*/
    private String user_id;
    /*联系方式*/
    private String user_phone;
/*    *//*所属公司*//*
    private String company_name;*/
    /*职位状态 1 项目经理2 生产经理3 技术负责人4 施工员5 安全员6 质量员7 材料员8 资料员9 预算员10 试验员11 劳务员12 测量员*/
    private Integer role;
    private Integer status;
    /*项目id*/
    private String project_id;
}
