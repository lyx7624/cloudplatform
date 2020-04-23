package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author WuJieFeng
 * @date 2019/11/19 14:32
 */
@Data
@Entity
@Table(name = "model_change")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Model_change {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;
    /*项目id*/
    private String project_id;
    /*模型表id*/
    private String mid;
    /*变更文件名*/
    private String file_name;
    /*对应构件id*/
    private String component_id;
    /*备注*/
    private String remark;
    /*创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date create_time;
    /*构件文件地址*/
    private String file_url;
    /*状态*/
    private Integer statu;
}
