package com.zcyk.util;

import com.zcyk.pojo.Model;
import com.zcyk.dto.ZZJFileUp;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


/**
 * 功能描述:文件工具类 切割文件
 * 开发人员: xlyx
 * 创建日期: 2019/11/19 14:09
 */
public class MyFileUtils {

   private static ExecutorService executorService = Executors.newCachedThreadPool();



    public static List<ZZJFileUp> getSplitFile(Model model) throws IOException {//切记要设置fileurl的全路径

        List<ZZJFileUp> files = new ArrayList<>();


        //预分配文件所占用的磁盘空间，在磁盘创建一个指定大小的文件，“r”表示只读，“rw”支持随机读写

        RandomAccessFile raf = new RandomAccessFile(new File(model.getModel_url()), "r");

        //计算文件大小
        long length = raf.length();
        //计算文件切片后每一份文件的大小
        int count = (int)length / (1024*1024)+1;
        Long maxSize  = length / count;

        System.out.println("开始分片---->"+System.currentTimeMillis());

        long offset = 0L;//定义初始文件的偏移量(读取进度)
        //开始切割文件
        for(int i = 0; i < count-1 ; i++){ //count-1最后一份文件不处理

            //标记初始化
            long fbegin = offset;
            //分割第几份文件
            long fend = (i+1) * maxSize;
            //写入文件
            offset = getWrite(model.getModel_url(), i, fbegin, fend);
            //设置切片对象

            //文件大小要做处理，用length不能有.00

            ZZJFileUp oneFile =
                    new ZZJFileUp
                            (md5HashCode(new FileInputStream(model.getModel_url())),
                                    md5HashCode(new FileInputStream(model.getModel_url() + "(temporary)/" + i + ".rvt")),
                                    i, count, maxSize, ".rvt",model.getModel_file_name(),length , model.getTags(), i,
                                    maxSize, maxSize, maxSize, model.getModel_url() + "(temporary)",
                                    i +".rvt", model.getModel_url() + "(temporary)/" + i +".rvt", length);
            files.add(oneFile);
        }
        if (length - offset > 0) {
            //写入文件
            getWrite(model.getModel_url(), count-1, offset, length);
            ZZJFileUp oneFile =
                    new ZZJFileUp
                            (md5HashCode(new FileInputStream(model.getModel_url())),
                                    md5HashCode(new FileInputStream(model.getModel_url() + "(temporary)/" + (count-1) + ".rvt")),
                                    count-1, count, length - offset, ".rvt",model.getModel_file_name(),length , model.getTags(), count-1,
                                    length - offset, length - offset, length - offset, model.getModel_url() + "(temporary)",
                                    (count-1) +".rvt", model.getModel_url() + "(temporary)/" + (count-1) +".rvt", length);
            files.add(oneFile);
            System.out.println("分片完成---->"+System.currentTimeMillis());

        }


        return files;
    }



    public static Map<String,Object> getSplitFileSync(Model model) throws IOException {//切记要设置fileurl的全路径
        Map<String,Object> map = new HashMap<>();
        List<ZZJFileUp> files = new ArrayList<>();
        //预分配文件所占用的磁盘空间，在磁盘创建一个指定大小的文件，“r”表示只读，“rw”支持随机读写

        RandomAccessFile raf = new RandomAccessFile(new File(model.getModel_url()), "r");

        //计算文件大小
        long length = raf.length();
        //计算文件切片后每一份文件的大小
        int count = (int)length / (1024*1024)+1;
        Long maxSize  = length / count;

        map.put("count",count);
        map.put("maxSize",maxSize);
        map.put("fileSize",length);

        System.out.println(model.getModel_name()+"开始分片---->"+System.currentTimeMillis());

        long offset = 0L;//定义初始文件的偏移量(读取进度)
        //开始切割文件
        for(int i = 0; i < count-1 ; i++){ //count-1最后一份文件不处理

            int index = i;
            //标记初始化
            long fbegin = offset;
            //分割第几份文件
            long fend = (i+1) * maxSize;
            //写入文件
            offset = getWrite(model.getModel_url(), i, fbegin, fend);
            //设置切片对象

            //文件大小要做处理，用length不能有.00

        }
        if (length - offset > 0) {
            Long lastSize = length - offset;
            map.put("lastSize",lastSize);
            System.out.println(model.getModel_name()+"分片成功---->"+System.currentTimeMillis());
            //写入文件
            getWrite(model.getModel_url(), count-1, offset, length);

        }


        return map;
    }


    /**
     * 指定文件每一份的边界，写入不同文件中
     * @param file 源文件
     * @param index 源文件的顺序标识
     * @param  begin 开始指针的位置
     * @param end 结束指针的位置
     * @return long
     */
    public static long getWrite(String file,int index,long begin,long end){

        long endPointer = 0L;
        try {
            //申明文件切割后的文件磁盘
            RandomAccessFile in = new RandomAccessFile( new File(file), "r");
            //定义一个可读，可写的文件并且后缀名为.tmp的二进制文件
            boolean mkdir = new File(file+"(temporary)").mkdir();
            RandomAccessFile out = new RandomAccessFile(new File(file+"(temporary)/" + index + ".rvt"), "rw");

            //申明具体每一文件的字节数组
            byte[] b = new byte[1024];
            int n = 0;
            //从指定位置读取文件字节流
            in.seek(begin);
            //判断文件流读取的边界
            while(in.getFilePointer() <= end && (n = in.read(b)) != -1){
                //从指定每一份文件的范围，写入不同的文件
                out.write(b, 0, n);
            }

            //定义当前读取文件的指针
            endPointer = in.getFilePointer();

            //关闭输入流
            in.close();
            //关闭输出流
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return endPointer;
    }



    /**
     * java获取文件的md5值
     * @param fis 输入流
     * @return
     */
    public static String md5HashCode(InputStream fis) {
        try {
            //拿到一个MD5转换器,如果想使用SHA-1或SHA-256，则传入SHA-1,SHA-256
            MessageDigest md = MessageDigest.getInstance("MD5");

            //分多次将一个文件读入，对于大型文件而言，比较推荐这种方式，占用内存比较少。
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md.update(buffer, 0, length);
            }
            fis.close();
            //转换并返回包含16个元素字节数组,返回数值范围为-128到127
            byte[] md5Bytes  = md.digest();
            BigInteger bigInt = new BigInteger(1, md5Bytes);//1代表绝对值
            return bigInt.toString(16);//转换为16进制
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}