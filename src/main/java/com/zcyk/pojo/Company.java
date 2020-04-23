package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "t_company")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Company {
    private static final long serialVersionUID = 1L;
    /*企业id*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    @Column(name = "id")
    private String company_id;
    /*企业名称*/
    private String company_name;
    /*企业地址*/
    private String company_address;
    /*企业状态*/
    private Integer company_status;
    /*企业容量*/
    private BigDecimal company_capacity;
    /*企业创建时间*/
    private String company_createtime;
    /*企业联系人*/
    private String company_people;
    /*企业联系人电话*/
    private String company_phonenum;
    /*企业编码*/
    private String company_code;
    /*企业到期时间*/
    private String company_endtime;
    /*企业logo*/
    private String company_logo_url;
    /*验证码*/
    @Transient
    private String login_code;
    @Transient
    private BigDecimal company_size;
    @Transient
    private String size_type;

}
