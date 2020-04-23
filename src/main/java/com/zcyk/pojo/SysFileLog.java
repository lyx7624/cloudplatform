package com.zcyk.pojo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;


@Data
@Entity
@Table(name = "sys_filelog")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysFileLog {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;
    /*操作人*/
    private String user_name;
    /*操作*/
    private String operation;
    /*文件名*/
    private String file_name;
    /*文件类型*/
    private String file_type;
    /*操作时间*/
    private String operation_time;
    /*公司id*/
    private String company_id;


}
