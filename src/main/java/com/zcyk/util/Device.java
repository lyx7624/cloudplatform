package com.zcyk.util;

import lombok.Data;

import java.util.List;

/**
 * @author WuJieFeng
 * @date 2020/4/14 9:41
 */
@Data
public class Device {
    String deviceId;
    String status;
    List<Channel> channels;

}
