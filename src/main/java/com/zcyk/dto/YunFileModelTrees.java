package com.zcyk.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能描述: 模板树
 * 开发人员: xlyx
 * 创建日期: 2019/11/11 17:26
 */
@Data
public class YunFileModelTrees {

    String f_modulenodeid;
//    String f_moduleid;
    String f_title;
//    String f_pid;
//    String f_part;
//    String f_orderid;
//    List<YunFileModelTrees> child;



    /*表名*/
//    String f_part;
//    String f_projectnodeid;
//    String f_unitprojectid;

   public static  List<YunFileModelTrees> getModelTree(String json){
//       String substring = json.substring(2, json.length() - 2);//格式不对，去正确格式
       String javaJson = StringEscapeUtils.unescapeJava(json);//去除转义
       List<YunFileModelTrees> yunFileModelTrees = JSONObject.parseArray(javaJson, YunFileModelTrees.class);
/*        Map<String, List<YunFileModelTrees>> modeMap = yunFileModelTrees.stream().collect(Collectors.groupingBy(YunFileModelTrees::getF_pid));
        for (YunFileModelTrees modeTree : yunFileModelTrees) {//设置child
           modeTree.setChild(modeMap.get(modeTree.getF_modulenodeid()));
        }
       List<YunFileModelTrees> thisModeTree = new ArrayList<>();
       yunFileModelTrees.forEach(mt->{if(mt.getF_pid().equals(mt.getF_moduleid()))thisModeTree.add(mt);});//pid=modelid证明是一级目录*/
       return yunFileModelTrees;
    }


}