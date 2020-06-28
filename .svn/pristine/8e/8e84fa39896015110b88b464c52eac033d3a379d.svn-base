package com.zcyk.util;

import com.zcyk.dto.FileForm;
import com.zcyk.mapper.UploadFileMapper;
import com.zcyk.pojo.UploadFile;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author WuJieFeng
 * @date 2019/8/8 20:13
 */
@Component
public class File_upload {

    @Autowired
    UploadFileMapper uploadFileMapper;


    /**
     * 功能描述：检查文件状态
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/27 15:17
     * 参数：[ * @param null]
     * 返回值：
    */
    public  Map<String, Object> findByFileMd5(UploadFile uploadFile) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//        UploadFile uploadFile = uploadFileRepository.findByFileMd5(md5);
        Map<String, Object> map = null;
        if (uploadFile == null) {
            //没有上传过文件
            map = new HashMap<>();
            map.put("flag", 0);
            map.put("fileId",UUID.randomUUID().toString());
            map.put("date", simpleDateFormat.format(new Date()));
        } else {
            //上传过文件，判断文件现在还存在不存在
            File file = new File(uploadFile.getFile_path());

            if (file.exists()) {
                if (uploadFile.getFile_status() == 1) {
                    //文件只上传了一部分
                    map = new HashMap<>();
                    map.put("flag", 1);
                    map.put("fileId", uploadFile.getFile_id());
                    map.put("date", simpleDateFormat.format(new Date()));
                } else if (uploadFile.getFile_status() == 2) {
                    //文件早已上传完整
                    map = new HashMap<>();
                    map.put("flag" , 2);
                    map.put("fileName",uploadFile.getFile_name());
                    map.put("url",uploadFile.getFile_path());
                }
            } else {

                map = new HashMap<>();
                map.put("flag", 0);
                map.put("fileId", UUID.randomUUID().toString());
                map.put("date", simpleDateFormat.format(new Date()));
            }
        }
        return map;
    }


    /**
     * 功能描述：分片上传
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/27 15:32
     * 参数：[ * @param null]
     * 返回值：
    */

    public  Map<String, Object> realUpload(FileForm form, MultipartFile multipartFile, String contextPath) throws Exception {
        String action = form.getAction();
        String fileId = form.getUuid();
        Integer index = Integer.valueOf(form.getIndex());
        String partMd5 = form.getPartMd5();
        String md5 = form.getMd5();
        Integer total = Integer.valueOf(form.getTotal());
        String fileName = form.getName();
        String size = form.getSize();
        String suffix = NameUtil.getExtensionName(fileName);



        String saveDirectory = contextPath + fileId;
        String filePath = saveDirectory + File.separator + fileId + "." + suffix;
        //验证路径是否存在，不存在则创建目录
        File path = new File(saveDirectory);
        if (!path.exists()) {
            path.mkdirs();
        }
        //文件分片位置
        File file = new File(saveDirectory, fileId + "_" + index);

        //根据action不同执行不同操作. check:校验分片是否上传过; upload:直接上传分片
        Map<String, Object> map = null;
        if ("check".equals(action)) {
            String md5Str = FileMd5Util.getFileMD5(file);
            if (md5Str != null && md5Str.length() == 31) {
                System.out.println("check length =" + partMd5.length() + " md5Str length =" + md5Str.length() + "partMd5=" + partMd5 + "md5str" + md5Str);
                md5Str = "0" + md5Str;
            }
            if (md5Str != null && md5Str.equals(partMd5)) {
                //分片已上传过
                map = new HashMap<>();
                map.put("flag", "1");
                map.put("fileId", fileId);
                if(index != total)
                    return map;
            } else {
                //分片未上传
                map = new HashMap<>();
                map.put("flag", "0");
                map.put("fileId", fileId);
                return map;
            }
        } else if("upload".equals(action)) {
            //分片上传过程中出错,有残余时需删除分块后,重新上传
            if (file.exists()) {
                file.delete();
            }
//            multipartFile.transferTo(new File(saveDirectory, fileId + "_" + index));
            File fpFile = new File(saveDirectory, fileId + "_" + index);
            FileOutputStream os = null;
            byte[] b = new byte[10 * 1024 * 1024];
            int len;
            FileInputStream io = null;
            File f = multipartFileToFile(multipartFile);
            os = new FileOutputStream(fpFile);
            io = new FileInputStream(f);
            while ((len = io.read(b)) != -1) {
                os.write(b, 0, len);
            }
            os.close();
            io.close();
            delteTempFile(f);

            map = new HashMap<>();
            map.put("flag", "1");
            map.put("fileId", fileId);
            if(!index.equals(total)){
                return map;
            }
        }

        if (path.isDirectory()) {
            File[] fileArray = path.listFiles();
            if (fileArray != null) {
                if (fileArray.length == total) {

                        //分块全部上传完毕,合并
                        File newFile = new File(contextPath, fileId + "." + suffix);
                        FileOutputStream outputStream = null;
                        byte[] byt = new byte[10 * 1024 * 1024];
                        int len;
                        FileInputStream temp = null;//分片文件
                    try {
                        outputStream = new FileOutputStream(newFile, true);//文件追加写入
                        for (int i = 0; i < total; i++) {
                            int j = i + 1;
                            temp = new FileInputStream(new File(saveDirectory, fileId + "_" + j));
                            while ((len = temp.read(byt)) != -1) {
                                outputStream.write(byt, 0, len);
                            }
                        }
                        //关闭流
                        try {
                            if (temp != null) {
                                temp.close();
                            }
                        }catch (Exception e){
                            temp.close();
                            e.printStackTrace();
                        }
                        try {
                            if(outputStream!=null){
                                outputStream.close();
                            }
                        }catch (Exception e){
                            outputStream.close();
                            e.printStackTrace();
                        }

                    }catch (Exception e){
                        temp.close();
                        outputStream.close();
                        e.printStackTrace();
                    }finally {
                        File partFiles = new File(contextPath+fileId);
                        File[] files = partFiles.listFiles();
                        for(File file1:files){
                            System.gc();
                            boolean delete = file1.delete();
                            if(file1.exists()){
                               System.gc();
                               file1.delete();
                            }
                        }
                        System.gc();
                        System.out.println(partFiles.delete());
                    }
                    //修改FileRes记录为上传成功
                    UploadFile uploadFile = new UploadFile();
                    uploadFile.setFile_id(fileId);
                    uploadFile.setFile_status(2);
                    uploadFile.setFile_name(fileName);
                    uploadFile.setFile_md5(md5);
                    uploadFile.setFile_suffix(suffix);
                    uploadFile.setFile_path(contextPath+fileId + "." + suffix);
                    uploadFile.setFile_size(size);

                    uploadFileMapper.insert(uploadFile);

                    map=new HashMap<>();
                    map.put("fileId", fileId);
                    map.put("flag", "2");
//                    map.put("url",contextPath+fileId + "." + suffix);
                    map.put("fileName",fileName);
                    return map;
                } else if(index == 1) {
                    //文件第一个分片上传时记录到数据库
                    UploadFile uploadFile = new UploadFile();
                    uploadFile.setFile_md5(md5);
                    String name = NameUtil.getFileNameNoEx(fileName);
                    if (name.length() > 32) {
                        name = name.substring(0, 32);
                    }
                    uploadFile.setFile_name(name);
                    uploadFile.setFile_suffix(suffix);
                    uploadFile.setFile_id(fileId);
                    uploadFile.setFile_path(contextPath+fileId + "." + suffix);
                    uploadFile.setFile_size(size);
                    uploadFile.setFile_status(1);

                    uploadFileMapper.insert(uploadFile);
                }
            }
        }
        return map;
    }




    /**
     * 功能描述：multipartFile转File
     * 开发人员：Wujiefeng
     * 创建时间：2019/12/10 10:47
     * 参数：[ * @param null]
     * 返回值：
    */
    public static File multipartFileToFile(MultipartFile file) throws IOException {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除本地临时文件
     * @param file
     */
    public static void delteTempFile(File file) {
        if (file != null) {
            File del = new File(file.toURI());
            del.delete();
        }
    }
}
