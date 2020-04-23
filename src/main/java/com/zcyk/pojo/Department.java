package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Table(name = "t_department")
@Entity
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Department {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;

    /*部门名称*/
    private String department_name;
    /*部门容量*/
    private BigDecimal department_capacity;
    /*部门地址*/
    @JsonIgnore
    private String department_address;
    /*部门状态*/
    @JsonIgnore
    private Integer department_status;
    /*部门创建时间*/
    @JsonIgnore
    private String department_createtime;
    /*企业id*/
    private String company_id;
    /*部门编码*/
    @JsonIgnore
    private String department_code;
    /*部门概述*/
    @JsonIgnore
    private String department_describe;

    private String parent_id;

    @Transient
    private List<Department> children;


}