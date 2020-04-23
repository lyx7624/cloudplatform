package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "department_folder")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentFolder {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;

    /*文件夹名称*/
    private String folder_name;

    /*部门id*/

    private String department_id;

    /*文件夹编码*/
    @JsonIgnore
    private String code;

    /*文件夹大小*/
    private BigDecimal folder_size;


    /*文件夹创建人*/
    private String folder_createuser;

    /*创建时间*/
    private String folder_createtime;

    /*修改人*/
    private String folder_updateuser;

    /*修改时间*/
    private String folder_updatetime;

    /*文件夹状态*/
    @JsonIgnore
    private Integer folder_statu;

    @JsonIgnore
    private String parent_id;

    private String createuser_id;

    /*子文件夹*/
    @Transient
    List<DepartmentFolder> departmentFolders;

    /*文件*/
    @Transient
    List<File> files;


}