package com.zcyk.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述: 上传的切片文件参数
 * 开发人员: xlyx
 * 创建日期: 2019/11/19 15:10
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ZZJFileUp {
    /*文件hash*/
    private String FileHash;

    /*块hash*/
    private String ChunkHash;

    /*块编号*/
    private Integer ChunkNumber;

    /*总的块数*/
    private Integer TotalChunks;

    /*当前块的大小*/
    private Long CurrentChunkSize;
    /*文件类型*/
    private String FileExtention;

    /*文件名称*/
    private String FileName;

    /*文件大小*/
    private Long FileSize;

    /*标签*/
    private String Tags;

    /*当前流的块的编号*/
    private Integer flowChunkNumber;

    /*当前流的块的撒小*/
    private long flowChunkSize;

    /*当前块流的块大小*/
    private long flowCurrentChunkSize;

    /*流的大小*/
    private long flowTotalSize;

    /*流的标识符
（大小+名称+类型
用来删除临时文件夹
*/
    private String flowIdentifier;

    /*流文件名*/
    private String flowFilename;

    /*流的真实地址*/
    private String flowRelativePath;

    /*流的总大小*/
    private Long flowTotalChunks;

    /*块*/
//    private File Chunk;

    public Map<String,String> toMap(){
        Map<String,String> map= new HashMap<>();
        for(Field field : ZZJFileUp.class.getDeclaredFields()){
            try{
                map.put(field.getName(), field.get(this).toString());
            }catch(Exception e){

            }

        }
        return map;
    }


}