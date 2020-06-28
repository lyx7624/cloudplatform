package com.zcyk.service.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.zcyk.dto.ZZJ_Token;
import com.zcyk.dto.ZZJFileUp;
import com.zcyk.dto.ZZJItem;
import com.zcyk.mapper.ProjectModelMapper;
import com.zcyk.pojo.*;
import com.zcyk.service.ZZJFileService;
import com.zcyk.util.HttpClientUtils;
import com.zcyk.util.MyFileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static com.zcyk.util.MyFileUtils.md5HashCode;

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2019/11/19 10:55
 */
@Service
public class ZZJFileServiceImpl implements ZZJFileService {

    @Value("${contextPath}")
    String contextPath;

    @Autowired
    ProjectModelMapper projectModelMapper;



    /**
     * 功能描述：用户登录
     * 开发人员： lyx
     * 创建时间： 2019/11/19 11:24
     * 参数：
     * 返回值：
     * 异常：
     */
    public ZZJ_Token loginZZJ(String user_account, String user_password) throws IOException {
//        String md5Password = DigestUtils.md5DigestAsHex(user_password.getBytes());
        HashMap<String, String> map = new HashMap<>();
        map.put("username","zhichenyunke");
        map.put("password","1713DAD2033819707B1037E70D696A1C");
        String s = HttpClientUtils.doPostUrl("http://id.typeo.org/token", map,null);
        ZZJ_Token ZZJToken = JSONObject.parseObject(JSONObject.parseObject(s).getString("item"), ZZJ_Token.class);
        ZZJToken.setUser_id(user_account).setLogin_time(new Date().getTime());
        return ZZJToken;

    }
    /**
     * 功能描述：刷新token
     * 开发人员： lyx
     * 创建时间： 2019/11/19 11:24
     * 参数：
     * 返回值：
     * 异常：
     */
    public ZZJ_Token refreshToken(String user_account, String refresh_token, String authorization) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("refreshToken",refresh_token);
        String s = HttpClientUtils.doPostUrl("http://id.typeo.org/token", map, authorization);
        ZZJ_Token ZZJToken = JSONObject.parseObject(s, ZZJ_Token.class);
        ZZJToken.setUser_id(user_account).setLogin_time(new Date().getTime());
        return ZZJToken;
    }

    /**
     * 功能描述：上传文件
     * 开发人员： lyx
     * 创建时间： 2019/11/19 11:24
     * 参数：
     * 返回值：
     * 异常：
     */
    public ZZJFileResponse upFile(Model model, String authorization) throws Exception {

        List<ZZJFileUp> splitFiles = MyFileUtils.getSplitFile(model);//切成片
        String res = "";
        JSONObject jsonObject = null;
        for(ZZJFileUp zzjFileUp:splitFiles ){//循环上传
            Map<String, String> map = zzjFileUp.toMap();
            java.io.File mulpartFile = new java.io.File(zzjFileUp.getFlowRelativePath());
            res = HttpClientUtils.postFile(mulpartFile, "http://jsgl.zfcxjw.cq.gov.cn:6010/file/api/v1/FlowJs/Upload", map,authorization);
            System.out.println(res);
            jsonObject = JSONObject.parseObject(StringEscapeUtils.unescapeJava(res));
            if(jsonObject.getObject("code",String.class)!=null&&!"200".equals(jsonObject.getObject("code",String.class))){//返回结果
                throw new Exception("文件同步失败");
            }
        }
        //删除文件夹
        if(splitFiles.size()!=0){
            deleteFile(new File(splitFiles.get(0).getFlowIdentifier()));
        }
        //获取上传文件状态
        return jsonObject.getObject("item", ZZJFileResponse.class);//获取到返回结果;注意返回的文件名不是实际文件名，而是直接的rvt文件内部名
        //删除文件 注意同步过程中不予许再上传同步，不然新的在旧的上传完了会被删除

    }

    public ZZJFileResponse upFileSync(Model model, String authorization) throws Exception {
        Map<String, Object> splitFileSync = MyFileUtils.getSplitFileSync(model);//切成片
        Integer count = (Integer) splitFileSync.get("count");
        Long maxSize = (Long) splitFileSync.get("maxSize");
        Long fileSize = (Long) splitFileSync.get("fileSize");
        Long lastSize = (Long) splitFileSync.get("lastSize");
        ZZJFileUp zzjFileUp = new ZZJFileUp().setFileHash(md5HashCode(new FileInputStream(model.getModel_url()))).setTotalChunks(count)
                .setCurrentChunkSize(maxSize).setFileExtention(".rvt").setFileName(model.getModel_file_name()).setFileSize(fileSize).setTags( model.getTags())
                .setFlowChunkSize(maxSize).setFlowCurrentChunkSize(maxSize).setFlowTotalSize(maxSize).setFlowIdentifier(model.getModel_url() + "(temporary)")
                .setFlowTotalChunks(fileSize);
        String res = "";
        JSONObject jsonObject = null;
        System.out.println(model.getModel_name()+"开始同步筑智建");
        for(int i = 0;i<count;i++ ){//循环上传
            zzjFileUp.setChunkHash(md5HashCode(new FileInputStream(model.getModel_url() + "(temporary)/" + i + ".rvt")))
                    .setChunkNumber(i).setCurrentChunkSize(maxSize).setFlowChunkNumber(i)
                    .setFlowFilename(i+".rvt").setFlowRelativePath(model.getModel_url() + "(temporary)/" + i +".rvt");

            if(i==count-1){
                zzjFileUp.setCurrentChunkSize(lastSize).setFlowChunkSize(lastSize).setFlowCurrentChunkSize(lastSize).setFlowTotalSize(lastSize);
            }
            Map<String, String> map = zzjFileUp.toMap();
            java.io.File mulpartFile = new java.io.File(zzjFileUp.getFlowRelativePath());
            res = HttpClientUtils.postFile(mulpartFile, "http://jsgl.zfcxjw.cq.gov.cn:6010/file/api/v1/FlowJs/Upload", map,authorization);
            jsonObject = JSONObject.parseObject(StringEscapeUtils.unescapeJava(res));
            if(jsonObject.getObject("code",String.class)!=null&&!"200".equals(jsonObject.getObject("code",String.class))){//返回结果
                throw new Exception("文件同步失败");
            }
        }
        System.out.println(model.getModel_name()+"筑智建同步成功"+res);

        //删除文件夹
        deleteFile(new File(model.getModel_url() + "(temporary)"));

        //获取上传文件状态
        return jsonObject.getObject("item", ZZJFileResponse.class);//获取到返回结果;注意返回的文件名不是实际文件名，而是直接的rvt文件内部名
        //删除文件 注意同步过程中不予许再上传同步，不然新的在旧的上传完了会被删除

    }


    /**
     * 功能描述：返回上传状态
     * 开发人员： lyx
     * 创建时间： 2019/11/23 10:07
     * 参数：
     * 返回值：
     * 异常：
     */
    public ZZJItem getFileStatus(String hash, Integer totalChunks, String authorization) {
        try {
            Map map = new HashMap<String,String>();
            map.put("hash",hash);
            map.put("totalChunks",totalChunks.toString());
            String res = HttpClientUtils.doGet("http://jsgl.zfcxjw.cq.gov.cn:6010/file/api/v1/FlowJs/UploadFileStatus",map,authorization,null);
            String item = JSONObject.parseObject(res).getString("item");
            ZZJItem zzjItemItem = JSONObject.parseObject(item,ZZJItem.class);
            return zzjItemItem;
        }catch (Exception e){
            e.printStackTrace();
            return null;//报错误说明文件上传错误，直接重新上传
        }

    }

    /**
     * 功能描述：同步文件接口
     * 开发人员： lyx
     * 创建时间： 2019/11/23 10:37
     * 参数：
     * 返回值：
     * 异常：
     */
    public Integer sycFile(String hash, String authorization){
        try {
            Map map = new HashMap<String,Object>();
            map.put("Hash",hash);
            String res = HttpClientUtils.doPost("http://jsgl.zfcxjw.cq.gov.cn:6010/file/api/v1/FlowJs/Synchronous",map,authorization,"application/x-www-form-urlencoded");
            String item = JSONObject.parseObject(res).getString("item");
            Map<String, Object> innerMap = JSONObject.parseObject(item).getInnerMap();
            return (Integer) innerMap.get("staus");
        }catch (Exception e){
            e.printStackTrace();
            return null;//报错误说明文件上传错误，直接重新上传
        }

    }


    /**
     * 功能描述：转换项目组文件  未知其意
     * 开发人员： lyx
     * 创建时间： 2019/11/19 11:24
     * 参数：
     * 返回值：
     * 异常：
     */
    public String transformFile(ZZJFileResponse zzjFileResponse ,String authorization) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("code",606);
        return JSONObject.toJSONString(map);
        /*List<Object> list = new ArrayList<>();
        PageData pageData = new PageData();
        pageData.put("Hash",zzjFileResponse.getHash());
        pageData.put("id",zzjFileResponse.getId());
        pageData.put("Name",zzjFileResponse.getName());
        pageData.put("Path",zzjFileResponse.getPath());
        pageData.put("Size",zzjFileResponse.getSize());
        pageData.put("Tags",zzjFileResponse.getTags().split(","));
        pageData.put("Type","."+zzjFileResponse.getType());
        list.add(pageData);
        map.put("fileList",JSONObject.toJSON(list));
        map.put("modelGroupId",zzjFileResponse.getModelgroupid());
        System.out.println(JSONObject.toJSONString(map));
        return HttpClientUtils.doPostJson("http://jsgl.zfcxjw.cq.gov.cn:6010/file/api/v1/FileConvert/convert-model", JSONObject.toJSONString(map),null,authorization);*/
    }



    /**
     * 功能描述：转换项目组文件  未知其意
     * 开发人员： lyx
     * 创建时间： 2019/11/19 11:24
     * 参数：
     * 返回值：
     * 异常：
     */
    public String transformStatus(String modelGroupId,String taskId ,String authorization) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("modelGroupId",modelGroupId);
        map.put("taskId",taskId);
        System.out.println(map.toString());
        return HttpClientUtils.doGet("http://jsgl.zfcxjw.cq.gov.cn:6010/file/api/v1/FileConvert/convert-process", map,authorization,null);
    }




    /**
     * 功能描述：删除文件夹或者文件
     * 开发人员： lyx
     * 创建时间： 2019/11/21 11:11
     * 参数：
     * 返回值：
     * 异常：
     */
    public  void deleteFile(java.io.File file) {
        if(file.exists()) {//判断路径是否存在
            if(file.isFile()){//boolean isFile():测试此抽象路径名表示的文件是否是一个标准文件。
                file.delete();
            }else{//不是文件，对于文件夹的操作
                //保存 路径D:/1/新建文件夹2  下的所有的文件和文件夹到listFiles数组中
                java.io.File[] listFiles = file.listFiles();//listFiles方法：返回file路径下所有文件和文件夹的绝对路径
                for (java.io.File file2 : listFiles) {
                    /*
                     * 递归作用：由外到内先一层一层删除里面的文件 再从最内层 反过来删除文件夹
                     *    注意：此时的文件夹在上一步的操作之后，里面的文件内容已全部删除
                     *         所以每一层的文件夹都是空的  ==》最后就可以直接删除了
                     */
                    deleteFile(file2);
                }
            }
            file.delete();
        }else {
            System.out.println("该file路径不存在！！");
        }
    }


}