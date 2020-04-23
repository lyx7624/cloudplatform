package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author WuJieFeng
 * @date 2019/10/31 17:14
 */
@Data
@Entity
@Table(name = "t_model")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Model {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;
    /*模型名称*/
    private String model_name;
    /*项目id*/
    private String project_id;
    /*项目id*/
    private String unitproject_id;
    @Transient
    private String unitproject_name;
    /*access_key值*/
    private String access_key;
    /*模型id*/
    private String model_id;
    /*创建时间*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date create_time;
    /*创建人*/
    private String create_user;
    /*模型类型*/
    /**
     * 1：主体
     * 2：分体
     * 3：机电
     * 4：施工场地布置
     * 5：其他
     */
    private Integer model_type;
    /*模型大小*/
    private BigDecimal model_size;
    /*历史记录（版本）*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date version;

    /*模型文件url*/
    private String model_url;

    /*模型状态 0已经删除 1未同步 2已经同步*/
    private Integer statu;

    /*模型文件名*/
    private String model_file_name;
    /*模型文件id*/
    private String model_file_id;

    /*项目阶段*/
    private String project_period;
    /*项目类型*/
    private String project_type;

    @Transient
    private String tags;

    /*模型同步到建委状态 0未同步 1同步成功 2 同步失败*/
    @Column(name = "jwstatus")
    private Integer JWStatus;




}
