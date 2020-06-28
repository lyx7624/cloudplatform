package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Data
@Entity
@Table(name = "project_folder")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class ProjectFolder {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;

    private String project_id;

    private String code;

    private String folder_name;

    private BigDecimal folder_size;

    private String folder_createuser;
    private String createuser_id;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date folder_createtime;

    private String folder_updateuser;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date folder_updatetime;

    private Integer folder_statu;

    private String parent_id;





}