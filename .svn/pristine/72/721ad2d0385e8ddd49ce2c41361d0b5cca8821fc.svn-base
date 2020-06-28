package com.zcyk.pojo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * 功能描述: 消息类
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/9/4 9:14
 */


@Data
@Entity
@Table(name = "t_message")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TMessage {

    private static final long serialVersionUID = 1L;

    /*用户id*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;
    //标题
    private String title;
    //内容
    private String content;
    //创建者
    private String create_user;
    //创建时间
    private String create_time;
    //消息来源
    private String source;
    //消息状态
    private Integer status;
    //文件id
    private String file_id;
    //接收方： 1全部用户 2部分企业（企业全部） 3部分企业管理员（可以是全部） 3部分人员（可以是不同企业）
    private Integer acceptor_type;


}
