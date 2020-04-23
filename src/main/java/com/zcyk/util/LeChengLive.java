package com.zcyk.util;

import lombok.Data;

import java.util.List;

/**
 * @author WuJieFeng
 * @date 2020/4/17 9:56
 */
@Data
public class LeChengLive {
     String      liveToken; // [String]直播授权token
     Integer     liveStatus; // [int]直播状态（1：开启；2：暂停；3：流量不足）
     Integer     liveType; // [int]直播源类型（1：设备；2：流地址）
     String      deviceId; // [String]设备序列号
     String      channelId; // [String]通道号
     String      coverUpdate; // [int]视频封面更新频率（单位：s）
     List<Stream> streams;
}
