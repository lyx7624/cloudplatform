package com.zcyk.pojo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user_department")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDepartment {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;
    /*用户ID*/
    private String user_id;
    /*部门id*/
    private String department_id;
    /*是否是管理员*/
    private Integer isdepartmentmanager;


}