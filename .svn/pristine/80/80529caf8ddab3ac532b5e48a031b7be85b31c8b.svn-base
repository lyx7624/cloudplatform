package com.zcyk.service.serviceImpl;

import com.zcyk.dto.Download;
import com.zcyk.mapper.*;
import com.zcyk.pojo.*;
import com.zcyk.pojo.File;
import com.zcyk.service.*;
import com.zcyk.util.DownloadUtils;
import com.zcyk.dto.ResultData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author WuJieFeng
 * @date 2019/8/11 9:05
 */

@Service
@Transactional


public class ProjectfolderServiceImpl implements ProjectfolderService {
    @Autowired
    FileMapper fileMapper;

    @Autowired
    HttpServletRequest request;

    @Autowired
    UserService userService;

    @Autowired
    FileService fileService;

    @Autowired
    ProjectFolderMapper projectFolderMapper;

    @Autowired
    UserProjectRoleMapper userProjectRoleMapper;
    @Autowired
    FileLogService fileLogService;

    @Autowired
    UserMapper userMapper;
    @Value("${contextPath}")
    String contextPath;


    /**
     * 功能描述：添加项目父文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/11 10:28
     * 参数：[ project_id 项目id]
     * 返回值： com.zcyk.dto.ResultData
     */
    public void addProjectParentFolder(Project project, String user_id) {
        String create_user = project.getCreateuser_id();
        User nowUser = null;
        if(StringUtils.isBlank(create_user)){//如果有项目创建人 文件夹创建人就是他
            nowUser = userMapper.selectUserById(user_id);
        }else {
            nowUser = userMapper.selectUserById(create_user);
        }
        ProjectFolder projectFolder = new ProjectFolder();
        projectFolder.setParent_id(project.getId())
                .setFolder_createtime(new Date())
                .setFolder_createuser(nowUser.getUser_name())
                .setId(UUID.randomUUID().toString())
                .setFolder_statu(1)
                .setCreateuser_id(nowUser.getId())
                .setFolder_name(project.getProject_name())
                .setProject_id(project.getId());
        projectFolderMapper.insertSelective(projectFolder);

    }

    @Override
    public List<ProjectFolder> getProjectParentFolder(String project_id) {
        return projectFolderMapper.selectProjectFolderByParentId(project_id);
    }

    @Override
    public ProjectFolder getProjectFolder(String folder_id) {
        return projectFolderMapper.selectFolderById(folder_id);
    }

    /**
     * 功能描述：判断是否是该项目管理员
     * 开发人员：Wujiefeng
     * 创建时间：2019/9/16 10:45
     * 参数：[ * @param null]
     * 返回值：
     */
    public boolean jugle(User user,String id,int type){
        if(user.getIscompanymanager()!=null&&user.getIscompanymanager()==1){
            return true;
        }
        if(type ==0){//文件夹
            ProjectFolder projectFolder = projectFolderMapper.selectFolderById(id);
            Integer role = userProjectRoleMapper.selectRoleByPhoneAndProject(projectFolder.getProject_id(), user.getUser_account());/* .getIsprojectmanager(); */
            if(role <=4 ){//0创建人 1 项目经理2 生产经理3 技术负责人4
                if(projectFolder.getCreateuser_id()!=null && user.getId().equals(projectFolder.getCreateuser_id())){
                    return true;
                }
                return true;
            }
        }else {
            File  file = fileMapper.selectByPrimaryKey(id);
            if(user.getId().equals(file.getFile_createuser_id())){
               return true;
           }
        }
        return false;
    }

    /**
     * 功能描述：在当前目录下新建文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/11 10:39
     * 参数：[ folder_name , parent_id用户处于的文件夹id,project_id]
     * 返回值：com.zcyk.dto.ResultData
     */
    public ResultData addProjectFolder(ProjectFolder projectFolder,User nowUser) throws Exception {

        if (projectFolder.getParent_id() != null) {
            ProjectFolder pf = projectFolderMapper.selectFolderName(projectFolder.getFolder_name(), projectFolder.getParent_id());
            if (pf != null) {
                return new ResultData().setStatus("401").setMsg("此文件夹已存在");
            }
            projectFolder.setFolder_statu(1)
                    .setId(UUID.randomUUID().toString())
                    .setFolder_createuser(nowUser.getUser_name())
                    .setCreateuser_id(nowUser.getId())
                    .setFolder_updatetime(new Date())
                    .setFolder_createtime(new Date());
            int i = projectFolderMapper.insertSelective(projectFolder);
            if (i != 0) {
                fileLogService.inLog("新建", projectFolder.getFolder_name(), "文件夹");
                return new ResultData().setStatus("200").setMsg("添加成功");

            } else {
                return new ResultData().setStatus("400").setMsg("添加失败");
            }
        } else {//没的父id表示新建顶级文件夹，不能新建
            return new ResultData().setStatus("400").setMsg("顶级文件根据项目生成，不能新建");
        }
    }

    /**
     * 功能描述：根据用户查询该用户所有的项目文件夹及文件
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/11 10:46
     * 参数：[ ]
     * 返回值：
     */

    public Map<String, Object> getProjectFolderAndFileById(String projectfolder_id) {
        Map<String, Object> map = new HashMap<>();
        List<ProjectFolder> projectFolders = projectFolderMapper.selectChildFolderById(projectfolder_id);
        List<File> files = fileMapper.selectFileByFolderId(projectfolder_id);
        map.put("projectFolders", projectFolders);
        map.put("files", files);
        return map;
    }


    /**
     * 功能描述：根据电话号码获取用户所在的项目的文件夹
     * 开发人员： lyx
     * 创建时间： 2020/3/31 13:43
     * 参数：
     * 返回值：
     * 异常：
     */
    public List<ProjectFolder> getProjectFolderByUser(String user_account) {
        return projectFolderMapper.selectFolderByUser(user_account);
    }


    /**
     * 功能描述：递归删除项目文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/23 12:48
     * 参数：[ * @param null]
     * 返回值：
     */
    public boolean deleteFolder(String folder_id) {
        //删除文件夹下文件文件
        fileMapper.deleteFile(folder_id);
        //删除该文件件
        projectFolderMapper.updateProjectfolderStatu(folder_id);
        //删除字文件夹及文件
        List<ProjectFolder> projectFolders = projectFolderMapper.selectChildFolderById(folder_id);
        for (ProjectFolder projectFolder : projectFolders) {
            System.out.println(projectFolder.getFolder_name()+"删除成功");
            deleteFolder(projectFolder.getId());
        }
        return true;
    }

    /**
     * 功能描述：删除项目文件夹及文件
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/11 11:12
     * 参数：[ ]
     * 返回值：com.zcyk.dto.ResultData
     */
    public ResultData deleteProjectFolder(List<String> ids, String folder_id,User user) throws Exception {
        ResultData resultData = new ResultData();
        boolean isDelete = true;
        for (String id : ids) {
            if (StringUtils.isBlank(fileMapper.judgeFile(id, folder_id))) {//为文件夹,删除所有层级文件夹及文件
                if(jugle(user, id, 0)){
                    String folder_name = projectFolderMapper.selectByPrimaryKey(id).getFolder_name();
                    deleteFolder(id);
                    fileLogService.inLog("删除",StringUtils.isBlank(folder_name)?"": folder_name, "文件夹");
                }else {
                    isDelete = false;
                }
            } else {
                if(jugle(user, id, 1)){
                    String file_name = fileMapper.selectByPrimaryKey(id).getFile_name();
                    fileMapper.updateFileStatus(id, folder_id);
                    fileLogService.inLog("删除",StringUtils.isBlank(file_name)?"": file_name , "文件");
                }else {
                    isDelete = false;
                }
            }
        }
        if(!isDelete){
            resultData.setStatus("401").setMsg("部分文件夹删除失败：文件没有权限");
        }
        return resultData;
    }

    /**
     * 功能描述：修改项目文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/11 11:16
     * 参数：[ id 文件夹id，folder_name 新的文件夹名称]
     * 返回值： com.zcyk.dto.ResultData
     */
    public ResultData updateProjectFolder(ProjectFolder projectFolder) throws Exception {

        User nowUser =userService.getNowUser(request);
        ResultData rd = new ResultData();
        ProjectFolder pf1 = projectFolderMapper.selectByPrimaryKey(projectFolder.getId());
        if(pf1.getProject_id().equals(pf1.getParent_id())){
            return new ResultData().setStatus("402").setMsg("此文件夹通过修改项目名称修改");
        }
        /*判断文件夹重名*/
        ProjectFolder pf = projectFolderMapper.selectFolderName(projectFolder.getFolder_name(), pf1.getParent_id());
        if (pf != null && !pf.getId().equals(projectFolder.getId())) {
            return new ResultData().setStatus("401").setMsg("此文件夹已存在");
        }

        projectFolder.setFolder_updatetime(new Date())
                .setFolder_updateuser(nowUser.getUser_name());
        int i = projectFolderMapper.updateByPrimaryKeySelective(projectFolder);
        if (i != 0) {
            fileLogService.inLog("修改", projectFolder.getFolder_name(), "文件夹");
            rd.setStatus("200");
            rd.setMsg("修改成功");
        } else {
            rd.setStatus("400");
            rd.setMsg("修改失败");
        }
        return rd;
    }

    /**
     * 搜索项目文件夹及文件
     *
     * @param index
     * @param folder_id
     * @return
     */
    public Map<String, Object> searchProjectFolder(String index, String folder_id,User nowUser) {
        Map<String, Object> map = new HashMap<>();
        List<ProjectFolder> pfs = new ArrayList<>();
        if (folder_id == "") {
            List<UserProjectRole> userProjectRole = userProjectRoleMapper.selectUserProject(nowUser.getUser_account());
            for (UserProjectRole userProject : userProjectRole) {
                List<ProjectFolder> projectFolders = projectFolderMapper.searchProjectFolder(index, userProject.getProject_id());
                for (ProjectFolder projectFolder : projectFolders) {
                    pfs.add(projectFolder);
                }
            }
            map.put("Folder", pfs);
            return map;
        }
        List<File> indexfile = fileMapper.indexfile(folder_id, index);
        List<ProjectFolder> projectFolders = projectFolderMapper.searchProjectFolder(index, folder_id);
        map.put("Folder", projectFolders);
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
                ProjectFolder pf = projectFolderMapper.selectByPrimaryKey(d.getId());
                if (null != pf && null != pf.getParent_id()) {
                    ProjectFolder pfp = projectFolderMapper.selectByPrimaryKey(pf.getParent_id());
                    if (null != pfp)
                        downloadName = pfp.getFolder_name() + ".zip";
                }
            } else {//文件
                File file = fileMapper.selectByPrimaryKey(d.getId());
                if (null != file && null != file.getFolder_id()) {
                    ProjectFolder pfp = projectFolderMapper.selectByPrimaryKey(file.getFolder_id());
                    if (null != pfp)
                        downloadName = pfp.getFolder_name() + ".zip";
                }
            }
        }

        for (Download d : list) {//生成临时文件夹
            if (d.getType() == 1) {//文件夹
                ProjectFolder projectFolder = projectFolderMapper.selectByPrimaryKey(d.getId());
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
            if (in != null)
                in.close();
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
        List<ProjectFolder> projectFolders = projectFolderMapper.selectProjectFolderByParentId(projectfolder_id);
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

        for (ProjectFolder folder : projectFolders) {//递归创建文件夹及文件
            getAllFolder(folder.getId(), realPath + "/" + folder.getFolder_name());
        }
    }


}
