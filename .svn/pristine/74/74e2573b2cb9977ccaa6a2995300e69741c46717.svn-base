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
 * @date 2019/11/13 15:24
 */
@Data
@Entity
@Table(name = "model_version")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Model_version {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;
    /*模型表的Id*/
    private String mid;
    /*模型名称*/
    private String model_name;
    /*项目id*/
    private String project_id;
    /*access_key值*/
    private String access_key;
    /*模型id*/
    private String model_id;
    /*创建时间*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;
    /*创建人*/
    private String create_user;
    /*模型类型*/
    /**
     * 1：主体  155
     * 2：分体  60
     * 3：机电  30
     * 4：施工场地布置  30
     * 5：其他  20
     */
    private Integer model_type;
    /*模型大小*/
    private BigDecimal model_size;
    /*历史记录（版本）*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date version;
    /*模型文件url*/
    private String model_url;
    /*模型状态：0 删除
     *           1 正在使用*/
    private Integer statu;
    /*模型文件名*/
    private String model_file_name;
}
