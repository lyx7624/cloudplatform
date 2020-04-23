package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "t_file")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class File {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;

    private String folder_id;

    private String file_name;

    private String file_url;

    private String file_type;

    private BigDecimal file_size;

    private String file_createuser;


    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    private String file_createtime;

    private String file_updateuser;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    private Date file_updatetime;

    private Integer file_statu;

    private String file_createuser_id;


    private String file_size_type;
}