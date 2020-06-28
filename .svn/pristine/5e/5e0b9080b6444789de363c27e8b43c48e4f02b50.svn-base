package com.zcyk.service.serviceImpl;

import com.zcyk.dto.Download;
import com.zcyk.mapper.*;
import com.zcyk.pojo.*;
import com.zcyk.pojo.File;
import com.zcyk.service.DepartmentFolderService;
import com.zcyk.service.FileLogService;
import com.zcyk.service.FileService;
import com.zcyk.service.UserService;
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
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2019/8/9 10:03
 */
@Service
@Transactional
public class DepartmentFolderServiceImpl implements DepartmentFolderService {

    @Autowired
    DepartmentFolderMapper departmentFolderMapper;

    @Autowired
    DepartmentMapper departmentMapper;

    @Autowired
    FileMapper fileMapper;

    @Autowired
    HttpServletRequest request;

    @Autowired
    UserService userService;

    @Autowired
    FileService fileService;

    @Autowired
    UserDepartmentMapper userDepartmentMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    SysFilelogMapper sysFilelogMapper;

    @Autowired
    FileLogService fileLogService;
    @Value("${contextPath}")
    String contextPath;


    /**
     * 功能描述：判断当前登录人是否是部门管理员
     * 开发人员： lyx
     * 创建时间： 2019/8/27 9:16
     */

    private boolean judegManager(String id,int type) {
        User user = userService.getNowUser(request);
        Integer iscompanymanager = user.getIscompanymanager();
        if (iscompanymanager!=null && iscompanymanager == 1 ) {//如果是企业管理员拥有所有权限
            return true;
        }

        if(type == 0){//文件夹
            DepartmentFolder departmentFolder = departmentFolderMapper.selectByPrimaryKey(id);
            Integer integer = userDepartmentMapper.selectUserManage(user.getId(), departmentFolder.getDepartment_id());
            if (integer!=null && integer == 1 ) {//是部门管理员获取所有权限
                return true;
            }
            if(departmentFolder.getCreateuser_id() != null && user.getId().equals(departmentFolder.getCreateuser_id())){
                return true;
            }
        }else {
            File file = fileMapper.selectByPrimaryKey(id);
            if(file.getFile_createuser_id() != null && user.getId().equals(file.getFile_createuser_id())){
                return true;
            }
        }

        return false;
    }

    /**
     * 功能描述： 添加部门父文件夹
     * 开发人员： lyx
     * 创建时间： 2019/8/9 19:40
     * 参数： [department_id 部门id]
     * 返回值： com.zcyk.dto.ResultData
     */
    public ResultData addDepartmentParentFolder(Department department) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        User nowUser = userService.getNowUser(request);
        Date date = new Date();
        DepartmentFolder departmentFolder = new DepartmentFolder()
                .setId(UUID.randomUUID().toString())
                .setParent_id(department.getId())
                .setFolder_updatetime(dateFormat.format(date))
                .setFolder_statu(1)
                .setFolder_name(department.getDepartment_name() + "文件夹")
                .setDepartment_id(department.getId())
                .setFolder_updateuser(nowUser.getUser_name());

        int i = departmentFolderMapper.insertSelective(departmentFolder);
        if (i != 0) {
            return new ResultData().setStatus("200").setMsg("添加成功");
        } else {
            return new ResultData().setStatus("400").setMsg("添加失败");
        }

    }


    /**
     * 功能描述：在当前目录下兴建文件夹
     * 开发人员： lyx
     * 创建时间： 2019/8/9 20:29
     * 参数： [folder_name  文件夹名, parent_id 用户处于的文件夹id]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    public ResultData addDepartmentFolder(DepartmentFolder departmentFolder) throws Exception {
        ResultData rd = new ResultData();
        if(departmentFolder.getParent_id()==null&&departmentFolder.getDepartment_id()==null){
            return rd.setMsg("新建失败，请联系管理员分配部门").setStatus("402");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        User user = userService.getNowUser(request);
        DepartmentFolder df = departmentFolderMapper.selectOneFilder(departmentFolder);
        if (df != null) {
            return new ResultData().setStatus("403").setMsg("此文件夹已存在");
        }
        Date date = new Date();
        departmentFolder.setId(UUID.randomUUID().toString())
                .setFolder_statu(1)
                .setFolder_createtime(dateFormat.format(date))
                .setCreateuser_id(user.getId())
                .setDepartment_id(departmentFolder.getDepartment_id())
                .setFolder_createuser(user.getUser_name());


        int i = departmentFolderMapper.insertSelective(departmentFolder);
        if (i != 0) {
            fileLogService.inLog("新增", departmentFolder.getFolder_name(), "文件夹");
            return new ResultData().setStatus("200").setMsg("添加成功");
        } else {
            return new ResultData().setStatus("400").setMsg("添加失败");
        }
    }


    /**
     * 功能描述：根据用户查询该用户所有的部门文件夹及文件
     * 开发人员： lyx
     * 创建时间： 2019/8/9 20:39
     * 参数： [d当前文件夹id]
     * 返回值： java.util.List<com.zcyk.pojo.DepartmentFolder>
     * 异常：
     */
    public Map<String, Object> selectAllDepartmentFolder(String departmentFolder_id) {
        Map<String, Object> map = new HashMap<>();
        User user = userService.getNowUser(request);
        if (departmentMapper.selectDepartmentByUser_Id(user.getId()) == null) {
            map.put("status", "400");
            map.put("msg", "你还没有部门");
            return map;
        }
        if (departmentFolder_id != null && !"".equals(departmentFolder_id)) {
            //查询该文件所有的文件夹
            List<DepartmentFolder> departmentFolders = departmentFolderMapper.selectDepartFloderByParentId(departmentFolder_id);
            //查询所有的文件
            List<File> files = fileMapper.selectFileByFolderId(departmentFolder_id);
            map.put("departmentFolders", departmentFolders);
            map.put("files", files);
            return map;
        } else {
            Department department = departmentMapper.selectDepartmentByUser_Id(user.getId());
            List<DepartmentFolder> dfs = departmentFolderMapper.selectDepartFloderByParentId(department.getId());
            List<DepartmentFolder> departmentFolders = departmentFolderMapper.selectDepartFloderByParentId(dfs.get(0).getId());
            List<File> files = fileMapper.selectFileByFolderId(dfs.get(0).getId());
            map.put("topDepartmentfolder", dfs);
            map.put("departmentFolders", departmentFolders);
            map.put("files", files);
            return map;
        }
    }

    /**
     * 功能描述：根据用户查询该用户所有的部门文件夹
     * 开发人员： liQiang
     * 创建时间： 2019/9/18 9:29
     * 参数： [当前文件夹id]
     * 返回值： 数据对象ResultData
     * 异常：
     */
    public ResultData selectAllDepartmentFolderForTree(String departmentFolder_id) {
        ResultData map = new ResultData();
        User user = userService.getNowUser(request);

        if (departmentMapper.selectDepartmentByUser_Id(user.getId()) == null) {
//            map.put("status", "400");
//            map.put("msg", "你还没有部门");
            return map.setStatus("400").setMsg("您还没有部门,请联系管理员为您添加部门！");
        }
        map.setStatus("200").setMsg("操作成功！");
        if (departmentFolder_id != null && !"".equals(departmentFolder_id)) {
            //查询该文件所有的文件夹
            List<DepartmentFolder> departmentFolders = departmentFolderMapper.selectDepartFloderByParentId(departmentFolder_id);
            //查询所有的文件
//            List<File> files = fileMapper.selectFileByFolderId(departmentFolder_id);
            map.setData(departmentFolders);
//            map.put("files", files);
            return map;
        } else {
            Department department = departmentMapper.selectDepartmentByUser_Id(user.getId());
            List<DepartmentFolder> dfs = departmentFolderMapper.selectDepartFloderByParentId(department.getId());
            List<DepartmentFolder> departmentFolders = departmentFolderMapper.selectDepartFloderByParentId(dfs.get(0).getId());
//            List<File> files = fileMapper.selectFileByFolderId(dfs.get(0).getId());
//            map.put("topDepartmentfolder", dfs);
            map.setData(departmentFolders);
//            map.put("files", files);
            return map;
        }
    }


    /**
     * 功能描述：获取文件夹内容
     * 开发人员： lyx
     * 创建时间： 2019/8/9 21:04
     * 参数： [id 当前文件夹id]
     * 返回值： com.zcyk.pojo.DepartmentFolder
     * 异常：
     */
//    public DepartmentFolder getAllDepartmentFolder(DepartmentFolder departmentFolder) throws Exception {
//        BigDecimal folderSize = new BigDecimal(0);
//        //当前文件夹文件大小
//        BigDecimal fileSize = (BigDecimal) fileService.folderSize().get("userSize");
//        folderSize.add(fileSize);
//
//        //设置字文件夹
//        List<File> files = fileMapper.selectFileByFolderId(departmentFolder.getId());
//        departmentFolder.setFiles(files);
//        Example example = new Example(DepartmentFolder.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("parent_id", departmentFolder.getId());
//        criteria.andEqualTo("folder_statu", 1);
//
//        //查询到改文件字文件夹
//        List<DepartmentFolder> departmentChildFolders = departmentFolderMapper.selectByExample(example);
//        departmentFolder.setDepartmentFolders(departmentChildFolders);
//        if (departmentChildFolders.size() != 0) {
//            for (DepartmentFolder df : departmentChildFolders) {
//                folderSize.add((BigDecimal) fileService.folderSize().get("userSize"));
//                getAllDepartmentFolder(df);
//            }
//        }
//        departmentFolder.setFolder_size(folderSize);
//        return departmentFolder;
//    }
//

    /**
     * 功能描述：递归删除文件夹寄文件
     * 开发人员： lyx
     * 创建时间： 2019/8/23 11:18
     */
    public boolean deleteFolder(String folder_id) {
        int fileNum = 0;
        //删除文件夹下文件文件
        fileNum = fileMapper.deleteFile(folder_id);
        //删除该文件夹
        departmentFolderMapper.deleteDepartmentfolder(folder_id);
        //删除字文件夹及文件
        List<DepartmentFolder> departmentFolders = departmentFolderMapper.selectDepartFloderByParentId(folder_id);
        for (DepartmentFolder departmentFolder : departmentFolders) {
            deleteFolder(departmentFolder.getId());
        }
        return true;

    }

    /**
     * 功能描述：删除部门下的文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/9 17:45
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData deleteDepartmentfolder(List<String> ids, String folder_id) throws Exception {
        ResultData rd = new ResultData();
        User user = userService.getNowUser(request);
        String msg = "";
        for (String id : ids) {
            if (StringUtils.isBlank(fileMapper.judgeFile(id, folder_id))) {//为文件夹,删除所有层级文件夹及文件
                if (!judegManager(id,0)) {//文件和文件夹来自同一个部门
                    msg = "部分文件夹没有权限";
                }
                deleteFolder(id);
                fileLogService.inLog("删除", departmentFolderMapper.selectByPrimaryKey(id).getFolder_name(), "文件夹");
            } else {
                if (!judegManager(id,1)) {//文件和文件夹来自同一个部门
                    msg = "部分文件没有权限";
                }
                fileMapper.updateFileStatus(id, folder_id);
                fileLogService.inLog("删除", fileMapper.selectByPrimaryKey(id).getFile_name(), "文件");
            }
        }
        if(StringUtils.isBlank(msg)){
            return rd.setStatus("200").setMsg("删除成功");//此处没有判断是否删除干净，不好判断
        }
        return rd.setStatus("201").setMsg(msg);//此处没有判断是否删除干净，不好判断

    }


    /**
     * 功能描述：修改部门文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/9 17:51
     * 参数：id 文件夹id,folder_name 新的文件夹名称
     * 返回值：
     */
    public ResultData updateDepartmentfolder(DepartmentFolder departmentFolder) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        User nowUser =  userService.getNowUser(request);

        ResultData rd = new ResultData();
        if (!judegManager(departmentFolder.getId(),0)) {
            rd.setMsg("没有权限");
            rd.setStatus("402");
            return rd;
        }
        Date date = new Date();
        String parent_id = departmentFolderMapper.selectByPrimaryKey(departmentFolder.getId()).getParent_id();
        DepartmentFolder df = departmentFolderMapper.selectOneFilder(departmentFolder.setParent_id(parent_id));
        if (df != null&&!df.getId().equals(departmentFolder.getId())) {
            return new ResultData().setStatus("401").setMsg("此文件夹已存在");
        }
        Example example = new Example(DepartmentFolder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", departmentFolder.getId());
        departmentFolder.setFolder_updatetime(dateFormat.format(date))
                .setFolder_updateuser(nowUser.getUser_name());
        int i = departmentFolderMapper.updateByExampleSelective(departmentFolder, example);
        if (i != 0) {
            rd.setStatus("200");
            rd.setMsg("修改成功");
            fileLogService.inLog("修改", departmentFolder.getFolder_name(), "文件夹");
        } else {
            rd.setStatus("400");
            rd.setMsg("修改失败");
        }
        return rd;
    }

    /**
     * 搜索部门文件夹及文件
     *
     * @param index
     * @param folder_id
     * @return
     */
    public Map<String, Object> searchDepartmentFolder(String index, String folder_id) {
        Map<String, Object> map = new HashMap<>();
        List<File> indexfile = fileMapper.indexfile(folder_id, index);
        List<DepartmentFolder> departmentFolders = departmentFolderMapper.searchDepartmentFolder(index, folder_id);
        map.put("Folder", departmentFolders);
        map.put("file", indexfile);
        return map;
    }

    /**
     * 功能描述：下载压缩包
     * 开发人员： lyx
     * 创建时间： 2020/3/31 16:01
     * 参数：
     * 返回值：
     * 异常：
     */
    @Override
    public void downloadFolderAndFileZip(List<Download> list) throws IOException {
        String realPath = contextPath + "/temp/" + UUID.randomUUID().toString().replace("-", "") + "/";//创建根目录
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
                DepartmentFolder pf = departmentFolderMapper.selectByPrimaryKey(d.getId());
                if (null != pf && null != pf.getParent_id()) {
                    DepartmentFolder pfp = departmentFolderMapper.selectByPrimaryKey(pf.getParent_id());
                    if (null != pfp)
                        downloadName = pfp.getFolder_name() + ".zip";
                }
            } else {//文件
                File file = fileMapper.selectByPrimaryKey(d.getId());
                if (null != file && null != file.getFolder_id()) {
                    DepartmentFolder pfp = departmentFolderMapper.selectByPrimaryKey(file.getFolder_id());
                    if (null != pfp)
                        downloadName = pfp.getFolder_name() + ".zip";
                }
            }
        }

        for (Download d : list) {//生成临时文件夹
            if (d.getType() == 1) {//文件夹
                DepartmentFolder projectFolder = departmentFolderMapper.selectByPrimaryKey(d.getId());
                if (null != projectFolder) {
                    java.io.File f = new java.io.File(realPath + projectFolder.getFolder_name());
                    if (!f.exists()) f.mkdirs();
                    getAllFolder(d.getId(), realPath + projectFolder.getFolder_name());
                }
            } else {//文件
                File file = fileMapper.selectByPrimaryKey(d.getId());
                if (null != file && null != file.getFile_url()) {
                    java.io.File f = new java.io.File(contextPath + file.getFile_url());
                    if (f.exists()) {
                        java.io.File t = new java.io.File(realPath + file.getFile_name());
                        if(t.createNewFile())DownloadUtils.copyFile(f, t);//复制
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
                // 针对IE或者以IE为内核的浏览器：
//            if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
//                zipName = java.net.URLEncoder.encode(zipName, "UTF-8");
//            } else {
//                // 非IE浏览器的处理：
//                zipName = new String(zipName.getBytes("UTF-8"), "ISO-8859-1");
//            }
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
                response.setStatus(400);
                outp.write("文件不存在无法下载");
                outp.flush();
                outp.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {//删除临时文件夹
            if (out != null)
                out.close();
            if (out != null)
                out.close();
//            System.out.println("删除文件夹");
            DownloadUtils.delete(new java.io.File(realPath));///删除文件夹
//            fileLogService.inLog("下载",projectFolderMapper.selectByPrimaryKey(list.get(0).getId()).getFolder_name(),"文件夹");//下载日志
        }
    }

    /**
     * 递归获取文件夹及其文件 并创建在临时文件夹里
     *
     * @param projectfolder_id
     * @param realPath
     * @return
     */
    public void getAllFolder(String projectfolder_id, String realPath) throws IOException {
        List<DepartmentFolder> projectFolders = departmentFolderMapper.selectDepartFloderByParentId(projectfolder_id);
        List<File> files = fileMapper.selectFileByFolderId(projectfolder_id);

        java.io.File file2 = new java.io.File(realPath);
        if(!file2.exists()){
            file2.mkdirs();
        }


        for (File f : files) {//将文件复制到该目录下
            java.io.File file = new java.io.File(contextPath + f.getFile_url());
            if (file.exists()) {
                java.io.File file1 = new java.io.File(realPath + "/" + f.getFile_name());

                DownloadUtils.copyFile(file,file1 );

            }
        }

        for (DepartmentFolder folder : projectFolders) {//递归创建文件夹及文件
            getAllFolder(folder.getId(), realPath + "/" + folder.getFolder_name());
        }
    }




}