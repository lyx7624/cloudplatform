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
@Table(name = "company_publicfolder")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyPublicfolder {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;
    /*文件夹名称*/
    private String folder_name;

    private String company_id;

    private String code;

    private BigDecimal folder_size;

    private String folder_createuser;

    private String createuser_id;

    private String folder_createtime;

    private String folder_updateuser;

    private Date folder_updatetime;

    private Integer folder_statu;

    private String parent_id;

    private List<File> files;

    private List<CompanyPublicfolder> companyPublicfolders;


}