package com.zcyk.pojo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "t_user")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private static final long serialVersionUID = 1L;

    /*用户id*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;
    /*用户账号*/
    protected String user_account;

    /*用户姓名*/
    protected String user_name;

    /*用户密码*/
    @JsonIgnore
    private String user_password;

    /*用户创建时间*/
    private String user_createtime;
//    /*用户注册时间*/
    private String user_logintime;
    /*用户状态 0已创建 1已注册 2未注册未创建*/

    private Integer user_status;
    /*用户所在企业id*/
    private String  company_id;

//    /*用户编码*/
//    private String  user_code;

    /*是否是管理员*/
    private Integer  iscompanymanager;

    /*性别 0女 1男*/
    private Integer  sex;

    /*是否是管理员*/
    @Transient
    private Integer  isdepartmentmanager;


    /*验证码*/
    @Transient
    private String login_code;

    /*容量*/
    @Transient
    private BigDecimal foldersize;

    /*容量单位*/
    @Transient
    private String size_type;



}
