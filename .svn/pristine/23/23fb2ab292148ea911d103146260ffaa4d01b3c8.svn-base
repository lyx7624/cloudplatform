package com.zcyk.dto;

import lombok.Data;

/**
 * @author WuJieFeng
 * @date 2020/4/20 11:30
 */
@Data
public class LiveList {
    //设备序列号,存在英文字母的设备序列号，字母需为大写
    String deviceSerial;
    //通道号
    Integer channelNo;
    //设备名称
    String deviceName;
    //HLS流畅直播地址
    String liveAddress;
    //HLS高清直播地址
    String hdAddress;
    //RTMP流畅直播地址
    String rtmp;
    //RTMP高清直播地址
    String rtmpHd;
    //地址使用状态：0-未使用或直播已关闭，1-使用中，2-已过期，3-直播已暂停，0状态不返回地址，其他返回。-1表示ret不返回200时的异常情况，参考ret返回错误码。
    Integer status;
    Long beginTime;
    Long endTime;
    Integer exception;

    String channel_name;
    String pic_url;


}
