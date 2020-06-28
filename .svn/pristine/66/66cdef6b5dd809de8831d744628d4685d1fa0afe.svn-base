package com.zcyk.service.serviceImpl;

import com.zcyk.dto.Download;
import com.zcyk.mapper.CompanyfolderMapper;
import com.zcyk.mapper.FileMapper;
import com.zcyk.mapper.UserMapper;
import com.zcyk.pojo.*;
import com.zcyk.pojo.File;
import com.zcyk.service.CompanyfolderService;
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
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author WuJieFeng
 * @date 2019/8/1 14:07
 */
@Service
@Transactional
public class CompanyfolderServiceImpl implements CompanyfolderService {
    @Autowired
    CompanyfolderMapper companyfolderMapper;
    @Autowired
    FileMapper fileMapper;
    @Autowired
    HttpServletRequest request;
    @Autowired
    UserService userService;
    @Autowired
    FileService fileService;
    @Autowired
    FileLogService fileLogService;


    @Autowired
    UserMapper userMapper;
    @Value("${contextPath}")
    String contextPath;






    /**
     * 功能描述：新建顶级文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/6 17:10
     * 参数：[ * @param null]
     * 返回值：
     */
    public void  addTopCompanyfolder(String company_id){
        User u = new User();
        CompanyPublicfolder companyPublicfolder = new CompanyPublicfolder();
        ResultData rd = new ResultData();
        /*文件夹创建日期*/
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        companyPublicfolder.setCompany_id(company_id)                   //文件夹企业id
                .setId(UUID.randomUUID().toString())        //文件夹id
                .setFolder_createtime(sdf.format(new Date()))//文件夹创建时间
                .setFolder_statu(1)                          //文件夹状态
                .setFolder_name("企业文件夹")
                .setParent_id(company_id);
        int i = companyfolderMapper.insert(companyPublicfolder);
    }


    /**
     * 功能描述：新建文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/6 17:10
     * 参数：[ 文件夹名称，文件夹父id]
     * 返回值：
     */
    public ResultData addCompanyfolder(CompanyPublicfolder companyPublicfolder) throws Exception {
        User nowUser = userService.getNowUser(request);
        if(1 != nowUser.getIscompanymanager())
        {
            return new ResultData().setStatus("401").setMsg("没有权限");
        }
        CompanyPublicfolder cf = companyfolderMapper.selectFolderName(companyPublicfolder.getFolder_name(),companyPublicfolder.getParent_id());
        if(cf !=null){
            return new ResultData().setStatus("402").setMsg("文件夹重名");
        }
        ResultData rd = new ResultData();

        /*文件夹创建日期*/
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        companyPublicfolder.setCompany_id(nowUser.getCompany_id())                   //文件夹企业id
                .setId(UUID.randomUUID().toString())        //文件夹id
                .setFolder_createuser(nowUser.getUser_name())   //文件夹创建人
                .setCreateuser_id(nowUser.getId())
                .setFolder_updatetime(new Date())
                .setFolder_createtime(sdf.format(new Date()))//文件夹创建时间
                .setFolder_statu(1);                          //文件夹状态
        int i = companyfolderMapper.insert(companyPublicfolder);
        if(i!=0){
            fileLogService.inLog("新建企业文件夹",companyPublicfolder.getFolder_name(),"文件夹");
            rd.setStatus("200");
            rd.setMsg("新建成功");
            rd.setData(companyPublicfolder);
        }else {
            rd.setStatus("400");
            rd.setMsg("新建失败");
        }
        return rd;
    }

    /**
     * 功能描述：修改文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/7 17:49
     * 参数：[文件夹id，文件夹名称]
     * 返回值：
     */
    public ResultData updateCompanyfolder(CompanyPublicfolder companyPublicfolder) throws Exception {
        ResultData rd = new ResultData();
//        userMapper.selectUserById(GetUserUtils.getUserId(request)).getIscompanymanager();
        User nowUser = userService.getNowUser(request);
        if(1 != nowUser.getIscompanymanager())
        {
            return new ResultData().setStatus("401").setMsg("您没有权限");
        }
        CompanyPublicfolder OcompanyPublicfolder = companyfolderMapper.selectByPrimaryKey(companyPublicfolder.getId());
        CompanyPublicfolder cf = companyfolderMapper.selectFolderName(companyPublicfolder.getFolder_name(),OcompanyPublicfolder.getParent_id());
        if(cf !=null && !cf.getId().equals(companyPublicfolder.getId())){
            return new ResultData().setStatus("402").setMsg("文件夹重名");
        }
        Example example = new Example(CompanyPublicfolder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",companyPublicfolder.getId());

        companyPublicfolder.setFolder_updateuser(nowUser.getUser_name())
                .setFolder_updatetime(new Date());
        int i = companyfolderMapper.updateByExampleSelective(companyPublicfolder, example);
        if(i != 0){
            fileLogService.inLog("修改",companyPublicfolder.getFolder_name(),"文件夹");
            rd.setMsg("修改成功");
            rd.setStatus("200");
        }else{
            rd.setStatus("400");
            rd.setMsg("修改失败");
        }
        return rd;
    }

    /**
     * 功能描述：查询公司文件夹及文件
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/23 14:27
     *
     * 参数：[ * @param null]
     * 返回值：
     */
    public Map<String,Object> Companyfolder(String companyfolder_id){
        Map<String,Object> map = new HashMap<>();
        if(companyfolder_id!=null&&!"".equals(companyfolder_id)){
            List<CompanyPublicfolder> companyPublicfolders = companyfolderMapper.selectCompanyfolderByParentId(companyfolder_id);
            List<File> files = fileMapper.selectFileByFolderId(companyfolder_id);
            map.put("companyfolders",companyPublicfolders);
            map.put("files",files);
            return map;
        }else {
            String company_id = userService.getNowUser(request).getCompany_id();
            List<CompanyPublicfolder> cfs = companyfolderMapper.selectCompanyfolderByParentId(company_id);
            List<CompanyPublicfolder> companyPublicfolders = companyfolderMapper.selectCompanyfolderByParentId(cfs.get(0).getId());
            List<File> files = fileMapper.selectFileByFolderId(cfs.get(0).getId());
            map.put("topCompanyfolders",cfs);
            map.put("companyfolders",companyPublicfolders);
            map.put("files",files);
            return map;
        }

    }

    /**
     * 功能描述：查询公司文件夹及文件
     * 开发人员：liQiang
     * 创建时间：2019/9/18 09:07
     * 参数：[ 文件夹id]
     * 返回值：
     */
    public List<CompanyPublicfolder> CompanyfolderForTree(String companyfolder_id) {
        List<CompanyPublicfolder> list =new ArrayList<>();
        if(companyfolder_id!=null&&!"".equals(companyfolder_id)){
            list = companyfolderMapper.selectCompanyfolderByParentId(companyfolder_id);
//            List<File> files = fileMapper.selectFileByFolderId(companyfolder_id);
//            map.put("companyfolders",companyPublicfolders);
//            map.put("files",files);
//            return map;
        }else {
            String company_id = userService.getNowUser(request).getCompany_id();
            List<CompanyPublicfolder> cfs = companyfolderMapper.selectCompanyfolderByParentId(company_id);
            list = companyfolderMapper.selectCompanyfolderByParentId(cfs.get(0).getId());
//            List<File> files = fileMapper.selectFileByFolderId(cfs.get(0).getId());
//            map.put("topCompanyfolders",cfs);
//            map.put("companyfolders",companyPublicfolders);
//            map.put("files",files);
//            return map;
        }
        return list;
    }

    /**
     * 功能描述：递归删除企业文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/23 12:31
     * 参数：[ * @param null]
     * 返回值：
     */
    public boolean deleteFolder(String folder_id){
        //删除文件夹下文件文件
        fileMapper.deleteFile(folder_id);
        //删除该文件件
        companyfolderMapper.updateCompanyfolderStatu(folder_id);
        //删除字文件夹及文件
        List<CompanyPublicfolder> companyPublicfolders = companyfolderMapper.selectCompanyfolderByParentId(folder_id);
        for(CompanyPublicfolder companyPublicfolder:companyPublicfolders){
            deleteFolder(companyPublicfolder.getId());
        }
        return true;
    }

    /**
     * 功能描述：企业文件夹及文件删除
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/7 19:05
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData deleteCompanyfolder(List<String>ids,String folder_id) throws Exception {

        User user = userService.getNowUser(request);
        for (String id:ids) {
            if(StringUtils.isBlank(fileMapper.judgeFile(id,folder_id))) {//为文件夹,删除所有层级文件夹及文件
                if(1 != userService.getNowUser(request).getIscompanymanager())
                {
                    return new ResultData().setStatus("401").setMsg("您没有权限");
                }
                deleteFolder(id);
              fileLogService.inLog("删除",companyfolderMapper.selectByPrimaryKey(id).getFolder_name(),"文件夹");
            }else {
               String file_creatuser = fileMapper.selectByPrimaryKey(id).getFile_createuser();
                Integer iscompanymanager = userService.getNowUser(request).getIscompanymanager();
                if (file_creatuser.equals(user.getUser_name())||1 == iscompanymanager) {
                    fileMapper.updateFileStatus(id, folder_id);
                    fileLogService.inLog("删除", fileMapper.selectByPrimaryKey(id).getFile_name(), fileMapper.selectByPrimaryKey(id).getFile_type());

                }else {
                    return new ResultData().setStatus("401").setMsg("您没有权限");
                }
            }
        }
        return null;
    }

    /**
     * 搜索企业文件夹及文件
     * @param index
     * @param folder_id
     * @return
     */
    public Map<String,Object> searchUserFolder(String index,String folder_id){
        Map<String,Object> map = new HashMap<>();
        List<File> indexfile = fileMapper.indexfile(folder_id, index);
        List<CompanyPublicfolder> companyPublicfolders = companyfolderMapper.searchCompanyFolder(index,folder_id);
        map.put("Folder",companyPublicfolders);
        map.put("file",indexfile);
        return map;
    }

    /**
     * 功能描述：批量下载文件夹和文件 并打包zip返回
     * 开发人员：LiQiang
     * 创建时间：2019/8/26 17:35
     * 参数：[ 文件夹id和文件id组成的list]
     * 返回值：
     * @param list
     */
    @Override
    public void companyFolderdownload(List<Download> list) throws IOException{
        String realPath = contextPath+"temp"+UUID.randomUUID().toString().replace("-","")+"/";//创建根目录
        java.io.File pathFile =new java.io.File(realPath);
        try {
            if(!pathFile.exists()){//如果文件夹不存在
                pathFile.mkdirs();//创建多级文件夹
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        String downloadName="下载.zip";
        if (null!=list.get(0)){//获取父文件夹的名称作为压缩包名称
            Download d =list.get(0);
            if(d.getType()==1){//文件夹
                CompanyPublicfolder cf =companyfolderMapper.selectByPrimaryKey(d.getId());
                if (null!=cf&&null!=cf.getParent_id()){
                    CompanyPublicfolder cfp = companyfolderMapper.selectByPrimaryKey(cf.getParent_id());
                    if (null!=cfp)
                        downloadName = cfp.getFolder_name()+".zip";
                }
            }else {//文件
                File file = fileMapper.selectByPrimaryKey(d.getId());
                if (null!=file&&null!=file.getFolder_id()){
                    CompanyPublicfolder cfp = companyfolderMapper.selectByPrimaryKey(file.getFolder_id());
                    if (null!=cfp)
                        downloadName = cfp.getFolder_name()+".zip";
                }
            }
        }

        for(Download d:list){//生成临时文件夹
            if(d.getType()==1){//文件夹
                CompanyPublicfolder companyFolder = companyfolderMapper.selectByPrimaryKey(d.getId());
                if(null!=companyFolder){
                    java.io.File f=new java.io.File(realPath+companyFolder.getFolder_name());
                    if(!f.exists())
                        f.mkdirs();
                    this.getAllFolder(d.getId(),realPath+companyFolder.getFolder_name());
                }
            }else{//文件
                File file = fileMapper.selectByPrimaryKey(d.getId());
                if (null!=file && null!=file.getFile_url()) {
                    java.io.File f=new java.io.File(contextPath+file.getFile_url());
                    if(f.exists()){
                        java.io.File t=new java.io.File(realPath+file.getFile_name());
                        if(t.createNewFile()){
                            DownloadUtils.copyFile(f,t);//复制
                        }
                    }
                }
            }

        }
        //下载临时文件夹的zip压缩包
        java.io.File targetZipFile =new java.io.File(realPath+downloadName);
        DataInputStream in=null;
        OutputStream out =null;
        try{
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletResponse response = servletRequestAttributes.getResponse();

            //生成压缩包
            List<String> path =new ArrayList<>();
            DownloadUtils.getPath(new java.io.File(realPath),path);
            if(path.size()>0){
                DownloadUtils.zipFile(path,targetZipFile,realPath);

                //下载压缩包
                String  zipName=targetZipFile.getName();
                // 以流的形式下载文件
                zipName = URLEncoder.encode(zipName,"UTF-8");
                String userAgent = request.getHeader("User-Agent");
                // 针对IE或者以IE为内核的浏览器：
//            if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
//                zipName = java.net.URLEncoder.encode(zipName, "UTF-8");
//            } else {
//                // 非IE浏览器的处理：
//                zipName = new String(zipName.getBytes("UTF-8"), "ISO-8859-1");
//            }
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-disposition", "attachment; filename=" +zipName);// 设定输出文件头
                response.setContentType("application/octet-stream");// 定义输出类型
//            Cookie fileDownload=new Cookie("fileDownload", "true");
//            fileDownload.setPath("/");
//            response.addCookie(fileDownload);
                //输入流：本地文件路径
                in = new DataInputStream(new FileInputStream(new java.io.File(realPath+downloadName)));
                //输出流
                out = response.getOutputStream();
                //输出文件
                int bytes = 0;
                byte[] bufferOut = new byte[1024];
                while ((bytes = in.read(bufferOut)) != -1) {
                    out.write(bufferOut, 0, bytes);
                }
                out.flush();
            }else{
                response.setContentType("application/json;charset=utf-8");
                Map<String,Object> map = new HashMap<String,Object>();
                PrintWriter outp = response.getWriter();
                outp.write("文件不存在无法下载");
                outp.flush();
                outp.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {//删除临时文件夹
            if (out != null)
                out.close();
            if (in != null)
                in.close();
//            System.out.println("删除文件夹");
            DownloadUtils.delete(new java.io.File(realPath));///删除文件夹
//            fileLogService.inLog("下载",companyfolderMapper.selectByPrimaryKey(list.get(0).getId()).getFolder_name(),"文件夹");//下载日志
        }
    }
    /**
     * 递归获取文件夹及其文件 并创建在临时文件夹里
     * @param companyfolder_id
     * @param realPath
     * @return
     */
    public void getAllFolder(String companyfolder_id,String realPath) throws IOException {
        List<CompanyPublicfolder> companyPublicfolders = companyfolderMapper.selectCompanyfolderByParentId(companyfolder_id);
        List<File> files = fileMapper.selectFileByFolderId(companyfolder_id);
        java.io.File file2 = new java.io.File(realPath);
        if(!file2.exists()){
            file2.mkdirs();
        }
        for(File f:files){//将文件复制到该目录下
            java.io.File file=new java.io.File(contextPath+f.getFile_url());
            if(file.exists()){
                DownloadUtils.copyFile(file,new java.io.File(realPath+"/"+f.getFile_name()));
            }
        }
        for (CompanyPublicfolder folder :companyPublicfolders){//递归创建文件夹及文件
            this.getAllFolder(folder.getId(),realPath+"/"+folder.getFolder_name());
        }
    }

}
