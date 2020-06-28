package com.zcyk.util;

import com.zcyk.pojo.User;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述: 上传excel获取表中的数据，表采用下载模板的形式
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 创建日期: 2019/7/23 10:40
 */
public class ReadExcel {
    public static List<User> readExcelContentz(MultipartFile file) throws Exception {
        List<User> userList = new ArrayList<>();
        // 工作表
        Workbook workbook = WorkbookFactory.create(file.getInputStream());

        // 表个数。
        int numberOfSheets = workbook.getNumberOfSheets();
        //时间转换


        // 遍历表。
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);

            // 行数。
            int rowNumbers = sheet.getLastRowNum() + 1;//表头第一行是标题

            // Excel第一行。
            Row temp = sheet.getRow(0);
            if (temp == null) {
                continue;
            }

            int cells = temp.getPhysicalNumberOfCells();

            // 读数据。
            for (int row = 1; row < rowNumbers; row++) {//第一行是标题
                Row r = sheet.getRow(row);
                User user = new User();
                Cell cell = r.getCell(1);
                DecimalFormat df = new DecimalFormat("#");
                String account = df.format(cell.getNumericCellValue());
                user.setUser_name(r.getCell(0).toString());
                user.setUser_account(account);

                userList.add(user);

            }
        }
        return userList;

    }

}
