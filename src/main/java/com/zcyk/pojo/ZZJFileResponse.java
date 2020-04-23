package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2019/11/19 10:30
 */
@Data
@Entity
@Table(name = "t_zzjfileresponse")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZZJFileResponse{

        private static final long serialVersionUID = 1L;

        /*用户id*/
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
        private String id;
        private String hash;
        private String name;
        private String path;
        @Column(name = "createdUserId")
        private String createdUserId;

        @Column(name = "createdUserName")
        private String createdUserName;
        private String type;//上传到筑业要去掉  .
        private String size;
        @Transient
        private String createdAt;
        private String url;
        private String tags;
        private Integer zytype;
        private String projectid;
        private String unitprojectid;
        /*分组id不知道何用先设置成项目id*/
        private String modelgroupid;

        /*总的块数*/
        @Transient
        private Integer TotalChunks;
        /*文件状态 zy*/
        @Transient
        private String taskid;


        /*文件状态 zy*/
        @Transient
        private String filestate;



        public void reSetZYType(int i){
                switch (i){
                        case 1:
                        case 2:
                        case 4:
                        case 5: this.setZytype(0);break;
                        case 3: this.setZytype(2);break;
                }
        }
        public Map<String,String> toMap(){
                Map<String,String> map= new HashMap<>();
                for(Field field : ZZJFileResponse.class.getDeclaredFields()){
                        try{
                                map.put(field.getName(), field.get(this).toString());
                        }catch(Exception e){

                        }

                }
                return map;
        }
}