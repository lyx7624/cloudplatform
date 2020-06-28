package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "user_folder")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserFolder {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;
    /*文件夹名称*/
    private String folder_name;
    /*用户id*/
    private String user_id;
    /*文件夹大小*/
    private BigDecimal folder_size;
    /*文件夹创建时间*/
    private String folder_createtime;
    /*文件夹修改时间*/
    private Date folder_updatetime;
    /*文件夹状态*/
    private Integer folder_statu;
    /*文件夹父id*/
    private String parent_id;
    /*创建人*/
    private String create_user;





}