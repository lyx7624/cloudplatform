package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "sys_log")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /*详情*/
    private String content;
    /*执行操作*/
    private String description;

    /*操作模块*/
    private String module;
    /*操作人*/
    private String username;

  /*操作时间*/
    private String updatetime;
}