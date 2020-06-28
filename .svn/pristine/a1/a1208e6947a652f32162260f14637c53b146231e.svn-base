package com.zcyk.service.serviceImpl;

import com.zcyk.mapper.*;
import com.zcyk.pojo.*;
import com.zcyk.pojo.File;
import com.zcyk.service.FileLogService;
import com.zcyk.service.FileService;
import com.zcyk.service.UserService;
import com.zcyk.util.DownloadUtils;
import com.zcyk.util.FileMd5Util;
import com.zcyk.util.File_upload;
import com.zcyk.dto.ResultData;
import com.zcyk.util.NameUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.common.Mapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.math.BigDecimal.*;

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2019/8/10 9:11
 */
@Service
@Transactional
public class FileServiceImpl implements FileService {
    @Autowired
    FileMapper fileMapper;
    @Autowired
    HttpServletRequest request;
    @Autowired
    UserMapper userMapper;

    @Autowired
    FileLogService fileLogService;

    @Autowired
    FileService fileService;

    @Autowired
    UploadFileMapper uploadFileMapper;

    @Value("${contextPath}")
    String contextPath;

    @Autowired
    UserfolderMapper userfolderMapper;


    @Autowired
    UserService userService;

    /**
     * 功能描述：获取个人用户容量
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/29 9:59
     * 参数：[ * @param null]
     * 返回值：
     */
    public Map<String, Object> folderSize(String user_id) {
        Map<String, Object> map = new HashMap<>();
        BigDecimal userSize = new BigDecimal(0);
        List<File> allfile = fileMapper.Allfile(user_id);
        for (File file : allfile) {
            userSize = userSize.add(file.getFile_size());
        }
        //统计个人容量
        BigDecimal a = new BigDecimal(1024);
        if (userSize.compareTo(a) == -1) {
            map.put("userSize", userSize);
            map.put("size_type", "B");
            return map;
        } else {
            userSize = userSize.divide(a, 0, ROUND_HALF_UP);
        }
        if (userSize.compareTo(a) == -1) {
            map.put("userSize", userSize);
            map.put("size_type", "KB");
            return map;
        } else {
            userSize = userSize.divide(a, 0, ROUND_HALF_UP);
        }
        if (userSize.compareTo(a) == -1) {
            map.put("userSize", userSize);
            map.put("size_type", "MB");
            return map;
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            userSize = userSize.divide(a, 2, ROUND_HALF_UP);
            map.put("userSize", userSize);
            map.put("size_type", "GB");
            return map;
        }


    }



    /**
     * 功能描述：修改文件名
     * 开发人员： lyx
     * 创建时间： 2019/8/10 9:22
     * 参数： [file_name 新的文件名, id 当前文件id, folder_id 文件id]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    public void updateFileName(File file) {
        fileMapper.updateFileName(file);

    }

    @Override
    public File findFile(File file) {
        return  fileMapper.selectFileName(file);
    }

    /**
     * 功能描述：删除单个文件
     * 开发人员： lyx
     * 创建时间： 2019/8/10 9:51
     * 参数： [id 文件id, folder_id 文件夹id]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    public ResultData deletedFile(String file_id, String folder_id) {


        Integer i = fileMapper.updateFileStatus(file_id, folder_id);
        if (i != 0) {
            return new ResultData().setStatus("200").setMsg("成功");
        } else {
            return new ResultData().setStatus("400").setMsg("失败");
        }
    }


    /**
     * 上传文件
     * 功能描述：
     * 开发人员： wjf
     * 创建时间： 2019/8/9 21:43
     * 参数： [file, departmentFolderId 文件夹id cover 是否覆盖]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */

    @Override
    public ResultData upFile(MultipartFile file, String FolderId,Integer cover) throws Exception {
        User nowUser = userService.getNowUser(request);

        File fileUploadLocal = null;
        //判断文件大小是否超出容量
        //计算可用容量
        BigDecimal companySize = new BigDecimal(0);
        BigDecimal allSizeGB = new BigDecimal("10737418240");
        if(userfolderMapper.selectByPrimaryKey(new UserFolder().setId(FolderId))!=null) {
            BigDecimal size1 = new BigDecimal(0);
            BigDecimal allSize = new BigDecimal(1024 * 1024 * 1024);//总容量 1024MB这里单位是K
            List<UserFolder> userFolders = userfolderMapper.selectUserfolderByparent_Id(nowUser.getId());
            for (UserFolder userFolder : userFolders) {
                List<File> files = fileMapper.selectFileByFolderId(userFolder.getId());
                for (File file1 : files) {
                    size1 = size1.add(file1.getFile_size());
                }
                userfolder_size(userFolder.getId());
            }
            BigDecimal userResidueSize = allSize.subtract(size1).setScale(1, ROUND_HALF_UP);
            if (new BigDecimal(file.getSize()).compareTo(userResidueSize) == 1) {
                return new ResultData().setStatus("406").setMsg("个人云盘空间不足");
            }
        }else {
            List<User> users = userMapper.selectAllUser(nowUser.getCompany_id());
            for (User user : users) {
                List<File> allfile = fileMapper.Allfile(user.getId());
                for (File file2 : allfile) {
                    companySize = companySize.add(file2.getFile_size());
                }
            }
            BigDecimal companyResidueSize = allSizeGB.subtract(companySize);
            if(new BigDecimal(file.getSize()).compareTo(companyResidueSize)==1){
                return new ResultData().setStatus("406").setMsg("企业云盘空间不足");
            }
        }


        String originalFilename = file.getOriginalFilename();
        originalFilename=originalFilename.substring(originalFilename.lastIndexOf("\\")==-1?0:originalFilename.lastIndexOf("\\")+1);

        List<File> files = fileMapper.selectFileByFolderId(FolderId);
        for (File nowFile : files) {
            if (nowFile.getFile_name().equals(originalFilename))
                if(cover!=null && cover !=0){//用户要求覆盖
                    fileUploadLocal = nowFile;
                    break;
                }else {
                    return new ResultData().setMsg("此文件名已存在").setStatus("401");
                }
        }
        String url = fileService.upFileToServer(file, contextPath, null);
        //将文件写入目标路径
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(fileUploadLocal==null){//没有要求覆盖就要生成新的记录
            fileUploadLocal = new File()
                    .setFile_name(originalFilename)
                    .setFile_createuser_id(nowUser.getId())
                    .setFile_size(new BigDecimal(file.getSize()))
                    .setFile_url(url)
                    .setFile_type(originalFilename.substring(originalFilename.lastIndexOf(".") + 1))
                    .setFile_statu(1)
                    .setFolder_id(FolderId)
                    .setFile_createtime(dateFormat.format(new Date()))
                    .setFile_createuser(nowUser.getUser_name())
                    .setId(UUID.randomUUID().toString());
        }else {//覆盖也要修改相关属性
            fileUploadLocal.setFile_url(url)
                    .setFile_name(originalFilename)
                    .setFile_createuser_id(nowUser.getId())
                    .setFile_size(new BigDecimal(file.getSize()))
                    .setFile_type(originalFilename.substring(originalFilename.lastIndexOf(".") + 1))
                    .setFile_createtime(dateFormat.format(new Date()))
                    .setFile_createuser(nowUser.getUser_name());
        }

        BigDecimal a = new BigDecimal(1024);
        BigDecimal b = new BigDecimal(1);
        BigDecimal size = new BigDecimal(file.getSize());
        if (size.compareTo(a) == -1) {
            fileUploadLocal.setFile_size_type(size + "B");
            int i = 0;
            if(cover != null && cover !=0){//用户要求覆盖
                i = fileMapper.updateByPrimaryKeySelective(fileUploadLocal);


            }else {//新增
                i = fileMapper.insertSelective(fileUploadLocal);
            }
            if (i != 0) {
                fileLogService.inLog("上传", fileUploadLocal.getFile_name(), fileUploadLocal.getFile_type());
                return new ResultData().setStatus("200").setMsg("添加成功").setData(url);
            } else {
                return new ResultData().setStatus("400").setMsg("添加失败");
            }
        } else {
            size = size.divide(a, 0, ROUND_HALF_UP);
        }
        if (size.compareTo(a) == -1) {
            fileUploadLocal.setFile_size_type(size + "KB");
            int i = fileMapper.insertSelective(fileUploadLocal);
            if (i != 0) {
                fileLogService.inLog("上传", fileUploadLocal.getFile_name(), fileUploadLocal.getFile_type());
                return new ResultData().setStatus("200").setMsg("添加成功").setData(url);
            } else {
                return new ResultData().setStatus("400").setMsg("添加失败");
            }
        } else {
            size = size.divide(a, 0, ROUND_HALF_UP);
        }
        if (size.compareTo(a) == -1) {
            fileUploadLocal.setFile_size_type(size + "MB");
            int i = fileMapper.insertSelective(fileUploadLocal);
            if (i != 0) {
                fileLogService.inLog("上传", fileUploadLocal.getFile_name(), fileUploadLocal.getFile_type());
                return new ResultData().setStatus("200").setMsg("添加成功").setData(url);
            } else {
                return new ResultData().setStatus("400").setMsg("添加失败");
            }
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size.divide(a, 2, ROUND_HALF_UP);
            fileUploadLocal.setFile_size_type(size + "GB");
            int i = fileMapper.insertSelective(fileUploadLocal);
            if (i != 0) {
                fileLogService.inLog("上传", fileUploadLocal.getFile_name(), fileUploadLocal.getFile_type());
                return new ResultData().setStatus("200").setMsg("添加成功").setData(url);
            } else {
                return new ResultData().setStatus("400").setMsg("添加失败");
            }
        }


    }

    /**
     * 功能描述：统计个人云盘大小
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/28 16:27
     * 参数：[ * @param null]
     * 返回值：
    */
    public  BigDecimal user_size(String user_id,BigDecimal size){

        List<UserFolder> userFolders = userfolderMapper.selectUserfolderByUser_Id(user_id);
        for (UserFolder userFolder:userFolders){
            List<File> files = fileMapper.selectFileByFolderId(userFolder.getId());
            for(File file:files){
                size = size.add(file.getFile_size());
            }
        }
        return size;
    }

    /**
     * 功能描述：个人云盘已用容量
     * 开发人员：Wujiefeng
     * 创建时间：2019/9/19 15:39
     * 参数：[ * @param null]
     * 返回值：
     */
    public Map<String,Object> userfolder_size(String user_id){
        Map<String,Object>map = new HashMap<>();
        BigDecimal size = new BigDecimal(0);
        BigDecimal a = new BigDecimal(1024);
        BigDecimal b = new BigDecimal(1048576);//1024*1024
        BigDecimal c = new BigDecimal(1);
        BigDecimal allSizeMB = new BigDecimal(1024);//总容量 1024MB
        BigDecimal allSizeGB = new BigDecimal(1);
        String userfolder_size;
//        List<UserFolder> userFolders = userfolderMapper.selectUserfolderByparent_Id(user_id);
//        for (UserFolder userFolder:userFolders){
//            List<File> files = fileMapper.selectFileByFolderId(userFolder.getId());
//            for(File file:files){
//                size = size.add(file.getFile_size());
//            }
//            userfolder_size(userFolder.getId());
//        }
        BigDecimal allsize = user_size(user_id, size);
        size = allsize.divide(b);

        BigDecimal userResidueSize = allSizeMB.subtract(size).setScale(1, ROUND_HALF_UP);
        userfolder_size =size.setScale(1,ROUND_HALF_UP)+"MB";

             map.put("PersonalUsedCapacity",userfolder_size);
             map.put("PersonalUsableCapacity",userResidueSize+"MB");
             return map;

    }
    /**
     * 功能描述：企业已用容量
     * 开发人员：Wujiefeng
     * 创建时间：2019/9/24 9:15
     * 参数：[ * @param null]
     * 返回值：
    */
    public Map<String,Object> companySize(){
        ResultData rd = new ResultData();
        BigDecimal companySize = new BigDecimal(0);
        Map<String,Object>map = new HashMap<>();
        User nowUser  = userService.getNowUser(request);

        List<User> users = userMapper.selectAllUser(nowUser.getCompany_id());
        for (User user : users) {
            List<File> allfile = fileMapper.Allfile(user.getId());
            for (File file : allfile) {
                companySize = companySize.add(file.getFile_size());
//                System.out.println(size);
            }
        }
        //统计企业容量
        BigDecimal a = new BigDecimal(1024);
        BigDecimal b = new BigDecimal(1048576);//1024*1024
        BigDecimal c = new BigDecimal(1);
        BigDecimal allSizeMB = new BigDecimal(10240);//总容量 20480MB
        BigDecimal allSizeGB = new BigDecimal(10);
        companySize = companySize.divide(b).setScale(1,ROUND_HALF_UP);
        if (companySize.compareTo(a) == -1) {
             BigDecimal  companyResidueSize = allSizeMB.subtract(companySize).setScale(1,ROUND_HALF_UP);
             if(companyResidueSize.compareTo(a)==-1) {
                 map.put("companyUsableCapacity", companyResidueSize + "MB");
                 map.put(" companyUsedCapacity", companySize + "MB");
             }else {
                 map.put("companyUsableCapacity", companyResidueSize.divide(a).setScale(1,BigDecimal.ROUND_DOWN) + "GB");
                 map.put("companyUsedCapacity", companySize + "MB");
             }
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
             companySize = companySize.divide(a, 1, ROUND_HALF_DOWN);
             BigDecimal companyResidueSize = allSizeGB.subtract(companySize);
             if(companyResidueSize.compareTo(c)==-1) {
                 map.put("companyUsableCapacity", companyResidueSize.multiply(a) + "MB");
                 map.put("companyUsedCapacity", companySize + "GB");
             }else {
                 map.put("companyUsableCapacity", companyResidueSize + "GB");
                 map.put("companyUsedCapacity", companySize + "GB");
             }
        }
        return map;
    }


    /**
     * 功能描述：根据url找到文件
     * 开发人员： lyx
     * 创建时间： 2019/9/25 14:39
     * 参数：
     * 返回值：
     * 异常：
     */
    public File findFileByUrl(String Url) {
        return fileMapper.selectByUrl(Url);
    }


    @Override
    public void getImage(String path, HttpServletResponse response) throws IOException {
            FileInputStream fis = null;
            OutputStream out = null;
            response.setContentType("image/gif");
            out = response.getOutputStream();
            java.io.File file = new java.io.File(path);
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    /**
     * 功能描述：上传文件到服务器
     * 开发人员： lyx
     * 创建时间： 2019/9/25 14:39
     * 参数：
     * 返回值：
     * 异常：
     */
    public String upFileToServer(MultipartFile file,String contextPath,String fileName) throws Exception {

        //判断文件是否为空
        if (file.isEmpty()) {
            throw new IOException("文件为空");
        }

        //判断该文件是否已经上传
        String fileMD5 = FileMd5Util.getMultipartFileMd5(file);
        UploadFile uploadFile = uploadFileMapper.findFileByMd5(fileMD5);
        if(uploadFile!=null && uploadFile.getFile_md5()!=null){//说明文件已经上传
            System.out.println("文件已上传");
            return uploadFile.getFile_path();
        }else {
            //创建文件夹
            java.io.File fileFolder = new java.io.File(contextPath);
            if (!fileFolder.exists()) {
                fileFolder.mkdirs();
            }
            //获取文件字节流
            //获取path对象
            if(StringUtils.isBlank(fileName)){
                fileName = UUID.randomUUID().toString() + file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".") == -1 ? file.getOriginalFilename().length() : file.getOriginalFilename().indexOf("."));
            }
            java.io.File transFile = new java.io.File(contextPath  + fileName);
            file.transferTo(transFile);
            //写入记录
            UploadFile uploadFile1 = new UploadFile()
                    .setFile_md5(fileMD5)
                    .setCreate_time(new Date())
                    .setFile_id(UUID.randomUUID().toString())
                    .setFile_name(file.getOriginalFilename())
                    .setFile_path(fileName).setFile_size(String.valueOf(file.getSize()))
                    .setFile_status(1).setUpdate_time(new Date())
                    .setFile_suffix(NameUtil.getExtensionName(file.getOriginalFilename()));
            uploadFileMapper.insertSelective(uploadFile1);
            return fileName;
        }
    }




}