package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author WuJieFeng
 * @date 2019/12/30 16:20
 */
@Data
@Entity
@Table(name = "project_schedule_control")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project_schedule_control {
    @Id
    private String id;

    /*项目Id*/
    private String project_id;
    /*进度名称*/
    private String schedule_name;
    /*计划开始时间*/
    private Date scheduled_start_date;
    /*计划结束时间*/
    private Date scheduled_end_date;
    /*实际开始时间*/
    private Date actual_start_date;
    /*实际结束时间*/
    private Date actual_end_date;
    /*状态码*/
    private Integer status;
    /*单位工程id*/
    private String uinitproject_id;
    /*类型*/
    private Integer type;
}
