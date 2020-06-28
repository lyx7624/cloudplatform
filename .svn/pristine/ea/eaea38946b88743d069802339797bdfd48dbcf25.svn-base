package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "t_projectmodel")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;

    /*项目名称*/
    private String project_name;
    /*项目容量*/
    private BigDecimal project_capacity;
    /*创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date project_createtime;
    /*项目描述*/
    private String project_describe;
    /*项目地址*/
    private String project_address;
    /*项目状态 0删除 1施工中 2完功*/
    private Integer project_status;
    /*企业id*/
    private String company_id;
    /*项目编码*/
    private String project_code;
    /*面积*/
    private String buildingarea;


    /*是否是BIM项目*/
    private Integer is_bim;
    /*施工单位*/
    private String construction_unit;
    private String constructionunitresp;
    /*建设单位（开发商）*/
    private String developer_unit;
    private String buildingunitresp;
    /*监理单位*/
    private String supervising_unit;
    /*监理负责人*/
    private String supervising_user;
    /*检测单位*/
    private String detection_unit;
    /*检测人员*/
    private String detection_user;
    /*预混供应商*/
    private String gunk_provider;
    /*设计单位*/
    private String design_unit;
    private String designunitresp;
    /*勘测单位*/
    private String survey_unit;
    private String prospectunitresp;

    /*完功时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date fishwork_date;
    /*动工日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startwork_date;
    /*预计开始日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date plannedstart_date;
    /*预计结束日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date plannedend_date;
    /*区域*/
    private String area;
    /*位置坐标*/
    private String map_coordinates;
    /*项目概况图*/
    private String pic_url;
    /*模型id*/
    private String model_project_id;
    /*工程进度*/
    private Integer works;
    /*模型缩略图*/
    private String model_pic;

    /*装换进度 0 未转换 1转换中 2转换失败 3转换成功*/
    @Column(name = "transformStatus")
    private Integer transformStatus;

    @Column(name = "taskId")
    private String taskId;


    /*筑业云资料管理账号*/
    private String yunfile_account;

    private String createuser_id;



    //返回给前端蒙着
    @Transient
    private Integer ZZJStatus;


}