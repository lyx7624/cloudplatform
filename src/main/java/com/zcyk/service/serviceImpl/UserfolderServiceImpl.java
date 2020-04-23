package com.zcyk.service.serviceImpl;


import com.zcyk.mapper.FileMapper;
import com.zcyk.mapper.UserMapper;
import com.zcyk.mapper.UserfolderMapper;
import com.zcyk.dto.Download;
import com.zcyk.pojo.File;
import com.zcyk.pojo.User;
import com.zcyk.pojo.UserFolder;
import com.zcyk.service.FileLogService;
import com.zcyk.service.FileService;
import com.zcyk.service.UserService;
import com.zcyk.service.UserfolderService;
import com.zcyk.util.DownloadUtils;
import com.zcyk.dto.ResultData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author WuJieFeng
 * @date 2019/8/9 14:30
 */
@Service
@Transactional
public class UserfolderServiceImpl implements UserfolderService {

    @Autowired
    FileMapper fileMapper;
    @Autowired
    UserfolderMapper userfolderMapper;
    @Autowired
    HttpServletRequest request;
    @Autowired
    FileService fileService;
    @Autowired
    FileLogService fileLogService;
    @Value("${contextPath}")
    String contextPath;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService userService;

    /**
     * 功能描述：根据use_id查找顶级文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/9 20:30
     * 参数：[ user_id]
     * 返回值：
     */
    public UserFolder selectUserfolderByParentId(String user_id) {
        Example example = new Example(UserFolder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parent_id", user_id);
        criteria.andEqualTo("folder_statu", 1);
        UserFolder parentFolder = userfolderMapper.selectOneByExample(example);
        return parentFolder;
    }

    /**
     * 功能描述：查询个人文件夹及文件
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/23 15:39
     * 参数：[ * @param null]
     * 返回值：
     */
    public Map<String, Object> Alluserfolder(String userfolder_id) {
        User nowUser = userService.getNowUser(request);
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(userfolder_id)) {
            List<UserFolder> userFolders = userfolderMapper.selectUserfolderByparent_Id(userfolder_id);
            List<File> files = fileMapper.selectFileByFolderId(userfolder_id);
            map.put("userFolders", userFolders);
            map.put("files", files);
            return map;
        } else {
            List<UserFolder> ufs = userfolderMapper.selectUserfolderByparent_Id(nowUser.getId());
            List<UserFolder> userFolders = userfolderMapper.selectUserfolderByparent_Id(ufs.get(0).getId());
            List<File> files = fileMapper.selectFileByFolderId(ufs.get(0).getId());
            map.put("topUserfolder", ufs);
            map.put("userFolders", userFolders);
            map.put("files", files);
            return map;
        }
    }

    /**
     * 功能描述：查询个人文件夹
     * 开发人员：liQiang
     * 创建时间：2019/9/18 08:55
     * 参数：[ 文件夹id]
     * 返回值：
     */
    public List<UserFolder> AlluserfolderForTree(String userfolder_id) {
        User nowUser =userService.getNowUser(request);
        Map<String, Object> map = new HashMap<>();
        List<UserFolder> list =new ArrayList<>();
        if (userfolder_id != null && !"".equals(userfolder_id)) {
            list = userfolderMapper.selectUserfolderByparent_Id(userfolder_id);
//            List<File> files = fileMapper.selectFileByFolderId(userfolder_id);
//            map.put("userFolders", userFolders);
//            map.put("files", files);

        } else {
            List<UserFolder> ufs = userfolderMapper.selectUserfolderByparent_Id(nowUser.getId());
            list = userfolderMapper.selectUserfolderByparent_Id(ufs.get(0).getId());
//            List<File> files = fileMapper.selectFileByFolderId(ufs.get(0).getId());
//            map.put("topUserfolder", ufs);
//            map.put("userFolders", userFolders);
//            map.put("files", files);
        }
        return list;
    }


    /**
     * 功能描述：新建个人顶级文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/9 14:58
     * 参数：[ 用户id user_id]
     * 返回值：
     */
    public ResultData addUserTopfolder(String user_id) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ResultData rd = new ResultData();
        UserFolder userFolder = new UserFolder();
        userFolder.setParent_id(user_id);
        //获取文件夹大小
        userFolder.setFolder_name("个人云盘文件夹")
                .setId(UUID.randomUUID().toString()) //文件夹id
                .setUser_id(user_id)                   //用户id
                .setFolder_size(new BigDecimal(1024 * 10000))//文件夹大小
                .setFolder_createtime(dateFormat.format(new Date()))      //文件创建时间
                .setFolder_statu(1);                   //文件状态
        int i = userfolderMapper.insert(userFolder);
        if (i != 0) {
            rd.setStatus("200");
            rd.setMsg("新建成功");
            rd.setData(userFolder);
        } else {
            rd.setStatus("400");
            rd.setMsg("新建失败");
        }
        return rd;
    }

    /**
     * 功能描述：新建文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/9 19:43
     * 参数：[ 文件夹名称、文件夹父id]
     * 返回值：
     */

    public ResultData addUserfolder(UserFolder userFolder) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserFolder uf = userfolderMapper.selectFolderName(userFolder.getFolder_name(), userFolder.getParent_id());
        if (uf != null) {
            return new ResultData().setStatus("401").setMsg("文件夹重名");
        }
        ResultData rd = new ResultData();
        /*获取用户id*/
        User user =userService.getNowUser(request);

        //获取文件夹大小
        userFolder.
                setId(UUID.randomUUID().toString()) //文件夹id
                .setUser_id(user.getId())                   //用户id
                .setFolder_createtime(dateFormat.format(new Date()))      //文件创建时间
                .setFolder_statu(1)
                .setCreate_user(user.getUser_name()==null?"":user.getUser_name());                   //文件状态
        int i = userfolderMapper.insert(userFolder);
        if (i != 0) {
            fileLogService.inLog("新增", userFolder.getFolder_name(), "文件夹");
            rd.setStatus("200");
            rd.setMsg("新建成功");
            rd.setData(userFolder);
        } else {
            rd.setStatus("400");
            rd.setMsg("新建失败");
        }
        return rd;
    }

    /**
     * 功能描述：递归删除个人文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/23 12:06
     * 参数：[ * @param null]
     * 返回值：
     */
    public boolean deleteFolder(String folder_id) {
        //删除文件夹下文件文件
        fileMapper.deleteFile(folder_id);
        //删除该文件夹
        userfolderMapper.updateUserfolderStatu(folder_id);
        //departmentFolderMapper.deleteDepartmentAllfolder(folder_id);
        //删除字文件夹及文件
        List<UserFolder> userFolders = userfolderMapper.selectUserfolderByparent_Id(folder_id);
        //List<DepartmentFolder> departmentFolders = departmentFolderMapper.selectDepartFloderByParentId(folder_id);
        for (UserFolder userFolder : userFolders) {
            deleteFolder(userFolder.getId());
        }


        return true;
    }

    /**
     * 功能描述：删除个人文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/9 15:18
     * 参数：[文件夹id userfoder_id]
     * 返回值：
     */
    public ResultData deleteUserfolder(List<String> ids, String folder_id) throws Exception {
        boolean b;
        Integer integer;
        for (String id : ids) {
            if (StringUtils.isBlank(fileMapper.judgeFile(id, folder_id))) {//为文件夹,删除所有层级文件夹及文件
                b = deleteFolder(id);
                fileLogService.inLog("删除", userfolderMapper.selectByPrimaryKey(id).getFolder_name(), "文件夹");
            } else {
                integer = fileMapper.updateFileStatus(id, folder_id);
                fileLogService.inLog("删除", fileMapper.selectByPrimaryKey(id).getFile_name(), "文件");
            }
        }
//        if(b=true&& )
        return null;
    }

    /**
     * 功能描述：修改个人文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/9 15:35
     * 参数：[ 文件夹id userfolder_id]
     * 返回值：
     */
    public ResultData updateUserfolder(UserFolder userFolder) throws Exception {
        UserFolder OuserFolder = userfolderMapper.selectByPrimaryKey(userFolder.getId());
        UserFolder uf = userfolderMapper.selectFolderName(userFolder.getFolder_name(),OuserFolder.getParent_id());
        if (uf != null&&!uf.getId().equals(userFolder.getId())) {
            return new ResultData().setStatus("401").setMsg("文件夹重名");
        }
        ResultData rd = new ResultData();
        Example example = new Example(UserFolder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", userFolder.getId());
        userFolder.setFolder_updatetime(new Date());
        int i = userfolderMapper.updateByExampleSelective(userFolder, example);
        if (i != 0) {
            fileLogService.inLog("修改", userFolder.getFolder_name(), "文件夹");
            rd.setStatus("200");
            rd.setMsg("修改成功");
        } else {
            rd.setStatus("400");
            rd.setMsg("修改失败");
        }
        return rd;
    }

    /**
     * 功能描述：搜索个人文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/26 10:46
     * 参数：[ * @param null]
     * 返回值：
     */
    public Map<String, Object> searchUserFolder(String index, String folder_id) {
        Map<String, Object> map = new HashMap<>();
        List<File> indexfile = fileMapper.indexfile(folder_id, index);
        List<UserFolder> userFolders1 = userfolderMapper.indexUserfolder(index,folder_id);
        map.put("Folder", userFolders1);
        map.put("file", indexfile);
        return map;
    }

    /**
     * 功能描述：批量下载文件夹和文件 并打包zip返回
     * 开发人员：LiQiang
     * 创建时间：2019/8/26 17:35
     * 参数：[ 文件夹id和文件id组成的list]
     * 返回值：
     *
     * @param list
     */
    @Override
    public void userFolderdownload(List<Download> list) throws IOException {
        String realPath = contextPath + "temp" + UUID.randomUUID().toString().replace("-", "") + "/";//创建根目录
        java.io.File pathFile = new java.io.File(realPath);
        try {
            if (!pathFile.exists()) {//如果文件夹不存在
                pathFile.mkdirs();//创建多级文件夹
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String downloadName = "下载.zip";
        if (null != list.get(0)) {//获取父文件夹的名称作为压缩包名称
            Download d = list.get(0);
            if (d.getType() == 1) {//文件夹
                UserFolder uf = userfolderMapper.selectByPrimaryKey(d.getId());
                if (null != uf && null != uf.getParent_id()) {
                    UserFolder ufp = userfolderMapper.selectByPrimaryKey(uf.getParent_id());
                    if (null != ufp)
                        downloadName = ufp.getFolder_name() + ".zip";
                }
            } else {//文件
                File file = fileMapper.selectByPrimaryKey(d.getId());
                if (null != file && null != file.getFolder_id()) {
                    UserFolder ufp = userfolderMapper.selectByPrimaryKey(file.getFolder_id());
                    if (null != ufp)
                        downloadName = ufp.getFolder_name() + ".zip";
                }
            }
        }

        for (Download d : list) {//生成临时文件夹
            if (d.getType() == 1) {//文件夹
                UserFolder userFolder = userfolderMapper.selectByPrimaryKey(d.getId());
                if (null != userFolder) {
                    java.io.File f = new java.io.File(realPath + userFolder.getFolder_name());
                    if (!f.exists())
                        f.mkdirs();
                    this.getAllFolder(d.getId(), realPath + userFolder.getFolder_name());
                }
            } else {//文件
                File file = fileMapper.selectByPrimaryKey(d.getId());
                if (null != file && null != file.getFile_url()) {
                    java.io.File f = new java.io.File(contextPath + file.getFile_url());
                    if (f.exists()) {
                        /*file.getFile_name改为 file.getFile_url*/
                        java.io.File t = new java.io.File(realPath + file.getFile_name());
                        DownloadUtils.copyFile(f, t);//复制
                    }
                }
            }

        }
        //下载临时文件夹的zip压缩包
        java.io.File targetZipFile = new java.io.File(realPath + downloadName);
        DataInputStream in = null;
        OutputStream out = null;
        try {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletResponse response = servletRequestAttributes.getResponse();

            //生成压缩包
            List<String> path = new ArrayList<>();
            DownloadUtils.getPath(new java.io.File(realPath), path);
            if (path.size() > 0) {
                DownloadUtils.zipFile(path, targetZipFile, realPath);

                //下载压缩包
                String zipName = targetZipFile.getName();
                // 以流的形式下载文件
                zipName = URLEncoder.encode(zipName, "UTF-8");
                String userAgent = request.getHeader("User-Agent");

//
//                if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
//                    zipName = new String(zipName.getBytes("UTF-8"), "ISO8859-1"); // firefox浏览器
//                } else if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
//                    zipName = URLEncoder.encode(zipName, "UTF-8");// IE浏览器
//                }else if (request.getHeader("User-Agent").toUpperCase().indexOf("CHROME") > 0) {
//                    zipName = new String(zipName.getBytes("UTF-8"), "ISO8859-1");// 谷歌
//                }

                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-disposition", "attachment; filename=" + zipName);// 设定输出文件头
                response.setContentType("application/octet-stream");// 定义输出类型
//            Cookie fileDownload=new Cookie("fileDownload", "true");
//            fileDownload.setPath("/");
//            response.addCookie(fileDownload);
                //输入流：本地文件路径
                in = new DataInputStream(new FileInputStream(new java.io.File(realPath + downloadName)));
                //输出流
                out = response.getOutputStream();
                //输出文件
                int bytes = 0;
                byte[] bufferOut = new byte[1024];
                while ((bytes = in.read(bufferOut)) != -1) {
                    out.write(bufferOut, 0, bytes);
                }
                out.flush();
            } else {
                response.setContentType("application/json;charset=utf-8");
                Map<String, Object> map = new HashMap<String, Object>();
                PrintWriter outp = response.getWriter();
                outp.write("文件不存在无法下载");
                outp.flush();
                outp.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {//删除临时文件夹
            if (out != null)
                out.close();
            if (in != null)
                in.close();
//            System.out.println("删除文件夹");
            DownloadUtils.delete(new java.io.File(realPath));///删除文件夹
//            fileLogService.inLog("下载",userfolderMapper.selectByPrimaryKey(list.get(0).getId()).getFolder_name(),"文件夹");//下载日志
        }
    }

    /**
     * 递归获取文件夹及其文件 并创建在临时文件夹里
     *
     * @param userfolder_id
     * @param realPath
     * @return
     */
    public void getAllFolder(String userfolder_id, String realPath) throws IOException {
        List<UserFolder> userFolders = userfolderMapper.selectUserfolderByparent_Id(userfolder_id);

        List<File> files = fileMapper.selectFileByFolderId(userfolder_id);
        java.io.File file1 = new java.io.File(realPath);
        if(!file1.exists()){
            file1.mkdirs();
        }
        for (File f : files) {//将文件复制到该目录下
            java.io.File file = new java.io.File(contextPath + f.getFile_url());
            if (file.exists()) {
                java.io.File file2 = new java.io.File(realPath + "/" + f.getFile_name());
                if(file2.createNewFile()){
                    DownloadUtils.copyFile(file, file2);
                }
            }
        }
        for (UserFolder folder : userFolders) {//递归创建文件夹及文件
            this.getAllFolder(folder.getId(), realPath + "/" + folder.getFolder_name());
        }
    }

}
