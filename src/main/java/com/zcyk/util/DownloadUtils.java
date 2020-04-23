package com.zcyk.util;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DownloadUtils {
    /**
     * 递归删除文件夹
     *
     * @param file
     */
    public static void delete(java.io.File file) {
        if (!file.exists()) return;

        if (file.isFile() || file.list() == null) {
            file.delete();
//            System.out.println("删除了"+file.getName());
        } else {
            java.io.File[] files = file.listFiles();
            for (java.io.File a : files) {
                delete(a);
            }
            file.delete();
//            System.out.println("删除了"+file.getName());
        }

    }

    /**
     * 将文件复制在
     *
     * @param sourceFile
     * @param targetFile
     * @throws IOException
     */
    public static void copyFile(java.io.File sourceFile, java.io.File targetFile) throws IOException {
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = null;
        BufferedInputStream inBuff = null;

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            input = new FileInputStream(sourceFile);
            inBuff = new BufferedInputStream(input);

            // 新建文件输出流并对它进行缓冲
            output = new FileOutputStream(targetFile);
            outBuff = new BufferedOutputStream(output);

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭流
            if (null != inBuff)
                inBuff.close();
            if (null != outBuff)
                outBuff.close();
            if (null != output)
                output.close();
            if (null != input)
                input.close();
        }
    }

    /**
     * 递归获取所有子文件夹路径及其下的文件
     *
     * @param rootFile
     * @param path
     */
    public static void getPath(java.io.File rootFile, List<String> path) {
        java.io.File[] files = rootFile.listFiles();
        if (null != files) {
            for (java.io.File file : files) {
                if (file.isDirectory()) {
                    path.add(file.getAbsolutePath());
                    getPath(file, path);
                } else {
                    path.add(file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 压缩生成文件夹及文件
     *
     * @param path
     * @param targetZipFile
     * @param base
     * @throws IOException
     */
    public static void zipFile(List<String> path, java.io.File targetZipFile, String base) throws IOException {
        // 根据给定的targetZipFile创建文件输出流对象
        FileOutputStream fos = new FileOutputStream(targetZipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);// 利用文件输出流对象创建Zip输出流对象
        byte[] buffer = new byte[1024];
        for (String string : path) {// 遍历所有要压缩文件的路径
            java.io.File currentFile = new java.io.File(string);
            if (currentFile.isDirectory()) {//文件夹
                ZipEntry entry = new ZipEntry(string.substring(base.length()) + "/");// 利用要压缩文件的相对路径创建ZipEntry对象
                zos.putNextEntry(entry);
                zos.closeEntry();// 关闭ZipEntry对象
            } else {//文件
                ZipEntry entry = new ZipEntry(string.substring(base.length()));// 利用要压缩文件的相对路径创建ZipEntry对象
                FileInputStream fis = new FileInputStream(currentFile);
                zos.putNextEntry(entry);
                int read = 0;
                while ((read = fis.read(buffer)) != -1) {// 将数据写入到Zip输出流中
                    zos.write(buffer, 0, read);
                }
                zos.closeEntry();// 关闭ZipEntry对象
                fis.close();
            }
        }
        zos.close();// 释放资源
        fos.close();
    }
}
