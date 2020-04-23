package com.zcyk.dto;

import lombok.Data;

/**
 * 海康设备通道信息
 * @author WuJieFeng
 * @date 2020/4/20 10:46
 */
@Data
public class CameraList {
    //设备序列号
    String deviceSerial;
    //IPC序列号
    String ipcSerial;
    //通道号
    Integer channelNo;
    //设备名
    String  deviceName;
    //通道名
    String  channelName;
    //在线状态：0-不在线，1-在线,-1设备未上报
    Integer status;
    String isShared;
    //图片地址
    String picUrl;
    //是否加密，0：不加密，1：加密
    Integer isEncrypt;
    //视频质量：0-流畅，1-均衡，2-高清，3-超清
    Integer videoLevel;
    //当前通道是否关联IPC：true-是，false-否。设备未上报或者未关联都是false
    Boolean relatedIpc;
}
