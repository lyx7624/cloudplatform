package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user_message")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserMessage {
    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;
    /*用户id*/
    private String user_id;
    /*消息id*/
    private String message_id;
    /*消息状态  消息状态：0 删除 1未读 2已读*/
    private Integer status;
    /*操作时间*/
    private String updateTime;


}
