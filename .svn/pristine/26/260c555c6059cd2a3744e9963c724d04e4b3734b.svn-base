package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * 视频监控设备表
 * @author WuJieFeng
 * @date 2020/4/20 14:33
 */
@Entity
@Data
@Accessors(chain = true)
@Table(name = "live_device")
@JsonInclude
public class LiveDevice {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;

    private String aid;

    private String device_name;

    private String device_id;

    private String device_code;

    private String status;
}
