package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 所有上传文件
 * @author WuJieFeng
 * @date 2019/11/27 15:29
 */
@Entity
@Data
@Accessors(chain = true)
@Table(name = "upload_file")
public class UploadFile {
    /* uuid */
    @Id
    @Column(name = "file_id")
    private String file_id;

    /* 文件路径 */
    @Column(name = "file_path")
    private String file_path;

    /* 文件大小 */
    @Column(name = "file_size")
    private String file_size;

    /* 文件后缀 */
    @Column(name = "file_suffix")
    private String file_suffix;

    /* 文件名字 */
    @Column(name = "file_name")
    private String file_name;

    /* 文件md5 */
    @Column(name = "file_md5")
    private String file_md5;


    /* 文件上传状态 */
    @Column(name = "file_status")
    private Integer file_status;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Column(name = "create_time")
    private Date create_time = new Date();

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Column(name = "update_time")
    private Date update_time;
}
