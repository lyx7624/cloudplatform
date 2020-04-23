package com.zcyk.service.serviceImpl;

import com.zcyk.mapper.FileMapper;
import com.zcyk.mapper.TSubrecordMapper;
import com.zcyk.mapper.TSubtableMapper;
import com.zcyk.mapper.UserMapper;
import com.zcyk.pojo.File;
import com.zcyk.pojo.TSubrecord;
import com.zcyk.pojo.TSubtable;
import com.zcyk.pojo.User;
import com.zcyk.service.SubProjectRecord;
import com.zcyk.service.UserService;
import com.zcyk.dto.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 功能描述:单位工程分布工程记录
 * 开发人员: lyx
 * 创建日期: 2019/10/9 15:25
 */
@Service
@Transactional
public class SubProjectRecordImpl implements SubProjectRecord {

    @Autowired
    TSubtableMapper tSubtableMapper;

    @Autowired
    UserService userService;

    @Autowired
    TSubrecordMapper tSubrecordMapper;

    @Autowired
    FileMapper fileMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    HttpServletRequest request;



    /**
     * 功能描述：添加单位工程添加分部工程记录 4条 表格18个
     * 开发人员： lyx
     * 创建时间： 2019/10/9 15:36
     * 参数：
     * 返回值：
     * 异常：
     */
    public ResultData addUnitprojectRecord(String id) {

        TSubrecord tSubrecord = new TSubrecord();
        //创建四条记录
        for (int i = 1; i < 5; i++) {
            tSubrecord.setId(UUID.randomUUID().toString())
                    .setStatus(1)
                    .setUnitpriject_id(id)
                    .setType(i);

            tSubrecordMapper.insertSelective(tSubrecord);
            // 资料类型 1质量验收记录 2核查记录 3安全。。。。 4观感质量验收记录
            TSubtable tSubtable = new TSubtable();
            for (int j = 1; j < 6; j++) {
                tSubtable.setData_type(j)
                        .setId(UUID.randomUUID().toString())
                        .setStatus(1)
                        .setSubrecord_id(tSubrecord.getId())
                        .setData_type(j);
                //添加表名 创建表格
                switch (i+""+j){
                    case "11" :
                    case "21" :
                    case "31" :
                        tSubtable.setTable_name("验收表-12");break;
                    case "12" :
                    case "22" :
                    case "32" :
                        tSubtable.setTable_name("验收表-13");break;
                    case "13" :tSubtable.setTable_name("验收表-12-1");break;
                    case "14" :tSubtable.setTable_name("验收表-12-2");break;
                    case "15" :tSubtable.setTable_name("验收表-12-3");break;
                    case "23" :tSubtable.setTable_name("验收表-12-4");break;
                    case "24" :tSubtable.setTable_name("验收表-12-5");break;
                    case "25" :tSubtable.setTable_name("验收表-12-6");break;
                    case "33" :tSubtable.setTable_name("验收表-12-28");break;
                    case "34" :tSubtable.setTable_name("验收表-12-29");break;
                    case "41" :tSubtable.setTable_name("验收表-8");break;
                    case "42" :tSubtable.setTable_name("验收表-9");break;
                    case "43" :tSubtable.setTable_name("验收表-10");break;
                    case "44" :tSubtable.setTable_name("验收表-11");break;
                }

                if(tSubtable.getTable_name()!=null){
                    tSubtableMapper.insertSelective(tSubtable);
                }

            }
        }
        return new ResultData().setStatus("200").setMsg("成功");
    }

    /**
     * 功能描述：根据id查看记录
     * 开发人员： lyx
     * 创建时间： 2019/10/10 9:47
     * 参数：
     * 返回值：
     * 异常：
     */
    public List<TSubrecord> selectRecordByUnitproject(String id) {
        List<TSubrecord> tSubrecords = tSubrecordMapper.selectByUnitprojectId(id);
        tSubrecords.forEach(tSubrecord -> {
            List<TSubtable> tSubtables = tSubtableMapper.selectByRecord(tSubrecord.getId());
            //返回个记录对应的所有表格
            tSubrecord.setTSubtables(tSubtables);

            //资料统计：查询出对应的表格id 资料类型 1质量验收记录 2核查记录 3安全。。。。 4观感质量验收记录
            //得到类型为1、2的表格id ->质量验收记录
            List<TSubtable> tSubtables1 = tSubtables.stream().filter(tSubtable -> tSubtable.getData_type() == 1 || tSubtable.getData_type() == 2).collect(Collectors.toList());
            //根据表格id找到对应的资料数量
            int tSubtables1Num = tSubtables1.stream().mapToInt(tSubtable1 -> fileMapper.selectFileByFolderId(tSubtable1.getId()).size()).sum();
            tSubrecord.setQuality_acceptance(tSubtables1Num);


            //得到类型为3的表格id ->核查记录
            List<TSubtable> tSubtables2 = tSubtables.stream().filter(tSubtable -> tSubtable.getData_type() == 3).collect(Collectors.toList());
            int tSubtables2Num = tSubtables2.stream().mapToInt(tSubtable1 -> fileMapper.selectFileByFolderId(tSubtable1.getId()).size()).sum();
            tSubrecord.setQuality_cqontrol(tSubtables2Num);


            //得到类型为4的表格id ->安全。。。。
            List<TSubtable> tSubtables3 = tSubtables.stream().filter(tSubtable -> tSubtable.getData_type() == 4).collect(Collectors.toList());
            int tSubtables3Num = tSubtables3.stream().mapToInt(tSubtable1 -> fileMapper.selectFileByFolderId(tSubtable1.getId()).size()).sum();
            tSubrecord.setSafety_function(tSubtables3Num);

            if(tSubrecord.getType()!=3){
                //得到类型为5的表格id ->观感质量验收记录 case3 建筑节能没的 没有
                List<TSubtable> tSubtables4 = tSubtables.stream().filter(tSubtable -> tSubtable.getData_type() == 5).collect(Collectors.toList());
                int tSubtables4Num = tSubtables4.stream().mapToInt(tSubtable1 -> fileMapper.selectFileByFolderId(tSubtable1.getId()).size()).sum();
                tSubrecord.setQuality_appearance(tSubtables4Num);
            }

        });
        return tSubrecords;
    }


    /** 应该写在 fileservice中的
     * 功能描述：查询表格文件
     * 开发人员： lyx
     * 创建时间： 2019/10/10 11:07
     * 参数：
     * 返回值：
     * 异常：
     */
    public List<File> selectTableFile(String id) {
        return fileMapper.selectFileByFolderId(id);
    }



    /** 应该写在 fileservice中的
     * 功能描述：从云上传文件到表格
     * 开发人员： lyx
     * 创建时间： 2019/10/10 11:16
     * 参数：
     * 返回值：
     * 异常：
     */
    public Integer insertFileByYun(File file) {
        User user =userService.getNowUser(request);

        //稍微修改一下，文件的上传人
        file.setFile_createuser(user.getUser_name()).setFile_createuser_id(user.getId());
        return fileMapper.insertSelective(file);
    }


    /**
     * 功能描述：根据单位工程id逻辑 删除文件 删除记录
     * 开发人员： lyx
     * 创建时间： 2019/10/10 14:17
     * 参数：
     * 返回值：
     * 异常：
     */
    public Integer deleteByUnitprojectId(String id) {
        User user =userService.getNowUser(request);

        List<TSubrecord> tSubrecords = tSubrecordMapper.selectByUnitprojectId(id);
        tSubrecords.forEach(tSubrecord ->{
            List<TSubtable> tSubtables = tSubtableMapper.selectByRecord(tSubrecord.getId());
            tSubtables.forEach(tSubtable -> fileMapper.deleteFile(tSubtable.getId()));
        });
        tSubrecordMapper.deleteByUnitprojectId(id);
        return null;

    }

}