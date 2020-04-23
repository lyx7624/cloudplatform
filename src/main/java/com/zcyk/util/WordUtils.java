package com.zcyk.util;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import java.io.File;
import java.util.Date;

/**
 * 功能描述: word转换
 * 开发人员: lyx
 * 创建日期: 2019/9/2 10:17
 */
public class WordUtils {

    public static void word2pdf(String docFilePath,String pdfFilePath) throws Exception {
        try {
            System.out.println(new Date()+"开始转换");
            //doc路径
            Document document = new Document(docFilePath);

            //pdf路径
            File outputFile = new File(pdfFilePath);
            //操作文档保存
            document.save(outputFile.getAbsolutePath(),SaveFormat.PDF);
        } catch (com.aspose.words.IncorrectPasswordException ie) {
            throw new Exception("有密码!");
        } catch (com.aspose.words.UnsupportedFileFormatException ufe) {
            throw new Exception(String.format("文件格式无法识别! {0}",
                    ufe.getMessage()));
        } catch (com.aspose.words.FileCorruptedException fce) {
            throw new Exception(String.format("文件损坏! {0}",
                    fce.getMessage()));
        } catch (Exception ex) {
            throw ex;
        }
        System.out.println(new Date()+"转换完成");

    }

    public static void pdf2word(String docFilePath,String pdfFilePath) throws Exception {
        try {
            //doc路径
            Document document = new Document(pdfFilePath);
            //pdf路径
            File outputFile = new File(docFilePath);
            //操作文档保存
            document.save(outputFile.getAbsolutePath(), SaveFormat.DOCX);
        } catch (com.aspose.words.IncorrectPasswordException ie) {
            throw new Exception("有密码!");
        } catch (com.aspose.words.UnsupportedFileFormatException ufe) {
            throw new Exception(String.format("文件格式无法识别! {0}",
                    ufe.getMessage()));
        } catch (com.aspose.words.FileCorruptedException fce) {
            throw new Exception(String.format("文件损坏! {0}",
                    fce.getMessage()));
        } catch (Exception ex) {
            throw ex;
        }
    }



}