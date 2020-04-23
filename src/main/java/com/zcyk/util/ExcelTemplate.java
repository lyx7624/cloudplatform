package com.zcyk.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* 功能描述:  excel 导出
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/12/27 10:41
*/
public class ExcelTemplate {


    /**
     * 功能描述：根据模板生成 excel 文件,因为一个模板中可能有多个工作表(Sheet)，所以遍历每一个 sheet，依次进行置换
     * 开发人员： lyx
     * 创建时间： 2019/12/27 10:50
     * 参数：
     * 返回值：
     * 异常：
     */
    public static void process(Object data, String templatePath, OutputStream os) {
        if (data == null || StringUtils.isEmpty(templatePath)) {
            return;
        }
        try {
            OPCPackage pkg = OPCPackage.open(templatePath);
            XSSFWorkbook wb = new XSSFWorkbook(pkg);
            Iterator<Sheet> iterable = wb.sheetIterator();
            while (iterable.hasNext()) {
                processSheet(data, iterable.next());
            }
            wb.write(os);
//            pkg.close();这个一旦关闭了，模板文件也会被重置
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }

    
    /**
     * 功能描述：置换列表。再次遍历 listRecord 中存储的单元格，从当前单元格开始依次向下置换，并应用 f 中存储的样式
     * 开发人员： lyx
     * 创建时间： 2019/12/27 10:51
     * 参数：
     * 返回值： 
     * 异常： 
     */
    private static void processSheet(Object data, Sheet sheet) {
        Map<Integer, Map<Integer, Cell>> listRecord = new LinkedHashMap<>();
        int lastRowNum = sheet.getLastRowNum();
        for (int i = lastRowNum; i >= 0; i--) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            int lastCellNum = row.getLastCellNum();
            for (int j = 0; j < lastCellNum; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                try {
                    String cellValue = cell.getStringCellValue();
                    if (cellValue.matches(".*\\$\\{[\\w.()]+}.*")) {
                        fillCell(cell, cellValue, data);
                    } else if (cellValue.matches(".*\\$\\{[\\w.]+\\[#][\\w.]+}.*")
                            ||cellValue.matches(".*\\$\\{[\\w.]+\\[#][\\w.]+[\\w.()]+[\\w.]+}.*")
                            ||cellValue.matches(".*\\$\\{[\\w.]+\\[#][\\w.]+[\\w.()]+[\\w.()]+}.*")){
                        Map<Integer, Cell> rowRecord = listRecord.computeIfAbsent(i, k -> new HashMap<>());
                        rowRecord.put(j, cell);
                    }
                } catch (Exception ignored) {

                }
            }
        }

        Map<String, List> listInData = new HashMap<>();
        //表格的格式集合
        Map<String, CellStyle> listCellStyle = new HashMap<>();

        Map<Cell, String> listCellPath = new HashMap<>();
        listRecord.forEach((rowNum, colMap) -> {
//            Pattern p = Pattern.compile("\\$\\{[\\w.\\[#\\]]+}");
            Set<String> listPath = new HashSet<>();
            colMap.forEach((colNum, cell) -> {
                String cellValue = cell.getStringCellValue();
//                Matcher m = p.matcher(cellValue);
//                if (m.find()) {
//                    String reg = m.group();
                    String regPre = cellValue.substring(2, cellValue.indexOf("["));
                    String regSuf = cellValue.substring(cellValue.lastIndexOf("].") + 2, cellValue.length() - 1);
                    listPath.add(regPre);
                    listCellStyle.put(String.format("%s.%s", regPre, regSuf), cell.getCellStyle());
                    listCellPath.put(cell, String.format("%s#%s", regPre, regSuf));
//                }
            });
            int maxRow = 0;
            //找到#前面的类 根据list生成行
            for (String s : listPath) {
                Object list = getAttributeByPath(data, s);
                if (list == null) {
                    list = new ArrayList<>();
                }
                if (list instanceof List) {
                    int len = ((List) list).size();
                    maxRow = maxRow > len ? maxRow : len;
                    listInData.put(s, ((List) list));//将#前数据放入
                } else {
                    throw new IllegalArgumentException(String.format("%s is not a list but a %s", s, list.getClass().getSimpleName()));
                }
            }
            if (maxRow > 1) {
                int endRow = sheet.getLastRowNum();
                sheet.shiftRows(rowNum + 1, endRow + 1, maxRow - 1);
            }
        });

        listRecord.forEach((rowNum, colMap) -> {
            colMap.forEach((colNum, cell) -> {
                String path = listCellPath.get(cell);
                String[] pathData = path.split("#");
                List list = listInData.get(pathData[0]);
                int baseRowIndex = cell.getRowIndex();
                int colIndex = cell.getColumnIndex();
                CellStyle style = listCellStyle.get(String.format("%s.%s", pathData[0], pathData[1]));

                for (int i = 0; i < list.size(); i++) {
                    int rowIndex = baseRowIndex + i;
                    Row row = sheet.getRow(rowIndex);
                    if (row == null) {
                        row = sheet.createRow(rowIndex);
                    }
                    row.setHeight((short) 799);//设置行高
                    Cell cellToFill = row.getCell(colIndex);

                    Object attribute = getAttributeByPath(list.get(i), pathData[1]);//这里就需要判断是不是意见数组，循环生成单元格
                    if (cellToFill == null) {
                        cellToFill = row.createCell(colIndex);
                    }
                    cellToFill.setCellStyle(style);
                    setCellValue(cellToFill,attribute);
                }
            });
        });
    }

    
    /**
     * 功能描述：置换单元格 fillCell(Cell, String, Object)
     * 开发人员： lyx
     * 创建时间： 2019/12/27 10:51
     * 参数：
     * 返回值： 
     * 异常： 
     */
    private static void fillCell(Cell cell, String expression, Object data) {
        Pattern p = Pattern.compile("\\$\\{[\\w.\\[\\]()]+}");
        Matcher m = p.matcher(expression);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String exp = m.group();
            String path = exp.substring(2, exp.length() - 1);
            Object value = getAttributeByPath(data, path);
            m.appendReplacement(sb, value == null ? "" : value.toString());
        }
        setCellValue(cell, sb.toString());
    }
    
    
    /**
     * 功能描述：给单元格设置值
     * 开发人员： lyx
     * 创建时间： 2019/12/27 10:52
     * 参数：
     * 返回值： 
     * 异常： 
     */
    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        } else if (value instanceof Character) {
            cell.setCellValue((Character) value);
        } else if (value instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     *
     * @param obj 访问对象
     * @param path 属性路径，形如(cls.type, cls.students.size())
     * @return
     */
    private static Object getAttributeByPath(Object obj, String path) {
        String[] paths = path.split("\\.");
        Object o = obj;
        for (String s : paths) {
            o = getAttribute(o, s);
        }
        return o;
    }


    /**
     * 功能描述：通过方法。属性。反射获取对象的属性值
     * 开发人员： lyx
     * 创建时间： 2019/12/27 10:52
     * 参数：
     * 返回值：
     * 异常：
     */
    private static Object getAttribute(Object obj, String member) {
        if (obj == null) {
            return null;
        }
        boolean isMethod = member.endsWith(")");
        boolean isNoParameterMethod = member.endsWith("()");
        if (!isMethod && obj instanceof Map) {
            return ((Map) obj).get(member);
        }
        try {
            Class<?> cls = obj.getClass();
            if ( !isNoParameterMethod && isMethod) {//这主要用于调用意见时使用
                Method method = cls.getDeclaredMethod(member.substring(0, member.length() - 3),int.class);//
                return  method.invoke(obj, Integer.parseInt(member.substring(member.indexOf("(") + 1, member.indexOf(")"))) );
            }else if(isNoParameterMethod){
                Method method = cls.getDeclaredMethod(member.substring(0, member.length() - 2));//
                return method.invoke(obj);
            } else {
                Field field = cls.getDeclaredField(member);
                field.setAccessible(true);
                return field.get(obj);
            }
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
//            e.printStackTrace(); 这里必然会导致索引越界
        }
        return null;
    }


    
}



