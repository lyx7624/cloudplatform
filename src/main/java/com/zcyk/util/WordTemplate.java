package com.zcyk.util;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.zcyk.pojo.ProjectDesignAlterationOpinion;
import com.zcyk.pojo.ProjectDesignAlterationPic;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import javax.imageio.ImageIO;



/**
 * 功能描述:  word 导出
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/12/27 10:41
 */
public class WordTemplate {

    /*文件艮路径*/
    String absolutePath;

    /*模板pic的路径*/
    String importance_pic= "/template/importance-pic/";


    private XWPFDocument document;

    public XWPFDocument getDocument() {
        return document;
    }

    public void setDocument(XWPFDocument document) {
        this.document = document;
    }

    /**
     * 初始化模板内容
     *            模板的读取流(docx文件)
     * @throws IOException
     *
     */
    public WordTemplate(InputStream inputStream,String absolutePath) throws IOException {
        this.absolutePath = absolutePath;
        document = new XWPFDocument(inputStream);
    }

    /**
     * 将处理后的内容写入到输出流中
     * @param outputStream
     * @throws IOException
     */
    public void write(OutputStream outputStream) throws IOException {
        document.write(outputStream);
    }






    /**
     * 功能描述：表格生成
     * 开发人员： lyx
     * 创建时间： 2019/12/26 16:44
     * 参数： dataMap需要替换的图片前的模板数据，picPath 图片  opinions 意见集合
     * 返回值：
     * 异常：
     */
    public void replaceDocument(Map<String, Object> dataMap) throws IOException, InvalidFormatException {

        if (!dataMap.containsKey("parametersMap")) {
            System.out.println("数据源(parametersMap)缺失");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> parametersMap = (Map<String, Object>) dataMap
                .get("parametersMap");

        List<IBodyElement> bodyElements = document.getBodyElements();// 所有对象（段落+表格）
        int templateBodySize = bodyElements.size();// 标记模板文件（段落+表格）总个数
        int curT = 0;// 当前操作表格对象的索引
        int curP = 0;// 当前操作段落对象的索引
        for (int a = 0; a < templateBodySize; a++) {
            IBodyElement body = bodyElements.get(a);
            if (BodyElementType.TABLE.equals(body.getElementType())) {// 处理表格
                XWPFTable table = body.getBody().getTableArray(curT);

                List<XWPFTable> tables = body.getBody().getTables();
                table = tables.get(curT);
                if (table != null) {
                    // 处理表格
                    List<XWPFTableCell> tableCells = table.getRows().get(0).getTableCells();// 获取到模板表格第一行，用来判断表格类型
                    String tableText = table.getText();// 表格中的所有文本
                    // 查找到##{foreach标签，该表格需要处理循环
                    if (tableCells.size() != 2
                            || !tableCells.get(0).getText().contains("##{foreach")
                            || tableCells.get(0).getText().trim().length() == 0) {
                        System.out
                                .println("文档中第"
                                        + (curT + 1)
                                        + "个表格模板错误,模板表格第一行需要设置2个单元格，"
                                        + "第一个单元格存储表格类型(##{foreachTable}## 或者 ##{foreachTableRow}##)，第二个单元格定义数据源。");
                        return;
                    }
                    String tableType = tableCells.get(0).getText();
                    String dataSource = tableCells.get(1).getText();
                    if (!dataMap.containsKey(dataSource)) {
                        System.out.println("文档中第" + (curT + 1) + "个表格模板数据源缺失");
                        return;
                    }

                    List<Map<String, Object>> tableDataList = (List<Map<String, Object>>) dataMap.get(dataSource);
                    if ("##{foreachTable}##".equals(tableType)) {
                        // System.out.println("循环生成表格");
                        addTableInDocFooter(table, tableDataList, parametersMap);
                    }
                    curT++;
                }
            } else if (BodyElementType.PARAGRAPH.equals(body.getElementType())) {// 处理段落
                // System.out.println("获取到段落");
                XWPFParagraph ph = body.getBody().getParagraphArray(curP);
                if (ph != null) {
                    // htmlText = htmlText+readParagraphX(ph);
                    addParagraphInDocFooter(ph, null, parametersMap, 0);

                    curP++;
                }
            }

        }
        // 处理完毕模板，删除文本中的模板内容
        for (int a = 0; a < templateBodySize; a++) {
            document.removeBodyElement(0);
        }

    }











    /**
     * 功能描述：表格循环和参数替换
     * 开发人员： lyx
     * 创建时间： 2019/12/26 16:43
     * 参数：
     * 返回值：
     * 异常：
     */
    public void addTableInDocFooter(XWPFTable templateTable, List<Map<String, Object>> list,
                                    Map<String, Object> parametersMap) throws IOException, InvalidFormatException {

        List<XWPFTableRow> templateTableRows = templateTable.getRows();// 获取模板表格所有行
        int tagRowsIndex = 0;// 标签行indexs
        for (int i = 0, size = templateTableRows.size(); i < size; i++) {
            String rowText = templateTableRows.get(i).getCell(0).getText();// 获取到表格行的第一个单元格
            if (rowText.contains("##{foreachTableRows}##")) {
                tagRowsIndex = i;
                break;
            }
        }
        // 表格整体循环
        for (Map<String, Object> map : list) {
            XWPFTable newCreateTable = document.createTable();// 创建新表格,默认一行一列
            //1.固定模板内容
            for (int i = 1; i < tagRowsIndex; i++) {
                XWPFTableRow newCreateRow = newCreateTable.createRow();
                CopyTableRow(newCreateRow, templateTableRows.get(i));// 复制模板行文本和样式到新行
            }

            //对图片进行调整
            List<Map<String, Object>> mapList = resetPic((List)map.get("picPaths"));
            //将占用一格和两个的区分出来,没有按照高度排序
            List<Map<String, Object>> twoCell = mapList.stream().filter(m -> m.get("type").equals(1.0)).collect(Collectors.toList());
            List<Map<String, Object>> oneCell = mapList.stream().filter(m -> m.get("type").equals(0.0)).collect(Collectors.toList());
            //找到需要循环行的下标
            //2.两个单元格一行的图片
            int oneRow = 0;//用于第一行和模板衔接
            int endRow = 0;//用于最后一行的衔接
            //判断一行两格的这个其中有没得单的
            if(twoCell.size()%2!=0){//有单的将单的那个单独放一行
                oneCell.add(0,twoCell.get(0));
                twoCell.remove(0);
            }
            for (int i = 0; i <twoCell.size(); i+=2) {
                endRow+=2;
                //新增一行
                XWPFTableRow newCreateRow = newCreateTable.createRow();
                if(endRow==mapList.size()){//如果既是第一行又是最后一行 7000优先
                    newCreateRow.setHeight(7600);
                }else if(oneRow==0){
                    newCreateRow.setHeight(7000);
                    oneRow++;
                }else {
                    newCreateRow.setHeight(16000);
                }

                //插入一行单元格
                for (int j = 0; j <9 ; j++) {
                    newCreateRow.addNewTableCell();
                }
                //合并单元格
                mergeCellsByCol(newCreateTable,6+i/2,0,4);
                mergeCellsByCol(newCreateTable,6+i/2,5,9);
                for (int k = 0; k <2 ; k++) {
                    //当前操作的对象
                    Map<String, Object> nowCell = twoCell.get(i + k);
                    //获取到合并后的第k单元格
                    XWPFTableCell cell = newCreateRow.getCell(k*5);
                    addPic(cell,nowCell);
                }
            }
            //3.一行的图片
            for (int i = 0; i <oneCell.size(); i++) {
                endRow++;
                //新增一行
                XWPFTableRow newCreateRow = newCreateTable.createRow();
                if(endRow==mapList.size()){//如果既是第一行又是最后一行 7000优先
                    newCreateRow.setHeight(7600);
                }else if(oneRow==0){
                    newCreateRow.setHeight(7200);
                    oneRow++;
                }else {
                    newCreateRow.setHeight(15000);
                }
                //插入一行单元格
                for (int j = 0; j < 9; j++) {
                    newCreateRow.addNewTableCell();
                }
                //合并单元格
                mergeCellsByCol(newCreateTable, 6+twoCell.size()/2+i, 0, 9);
                //当前操作的对象
                Map<String, Object> nowCell = oneCell.get(i);
                //获取到合并后的单元格
                XWPFTableCell cell = newCreateRow.getCell(0);
                addPic(cell,nowCell);
            }
            //4.意见的循环
            //获取每一个表格的意见
            List<ProjectDesignAlterationOpinion> opinions =(List) map.get("opinions");
            //说是必须有一条意见就算为空
            if(opinions.size()==0){
                opinions.add(new ProjectDesignAlterationOpinion().setBatch_number("1"));
            }


            //将意见转换成map
            List<Map<String, Object>> maps = new ArrayList<>();
            opinions.forEach(o->{
                HashMap<String, Object> opinion = new HashMap<>();
                opinion.put("sdari_opinion",StringUtils.isNotBlank(o.getSdari_opinion())?o.getSdari_opinion():"");
                opinion.put("sdari_dispose_user",StringUtils.isNotBlank(o.getSdari_dispose_user())?o.getSdari_dispose_user():"");
                opinion.put("sdari_dispose_date",StringUtils.isNotBlank(o.getSdari_dispose_date())?o.getSdari_dispose_date():"");
                opinion.put("bim_opinion",StringUtils.isNotBlank(o.getBim_opinion())?o.getBim_opinion():"");
                opinion.put("bim_dispose_user",StringUtils.isNotBlank(o.getBim_dispose_user())?o.getBim_dispose_user():"");
                opinion.put("bim_dispose_date",StringUtils.isNotBlank(o.getSdari_dispose_date())?o.getSdari_dispose_date():"");
                opinion.put("batch_number",StringUtils.isNotBlank(o.getBatch_number())?o.getBatch_number():"");
                maps.add(opinion);
            });
            //看看有多少的循环
            for (int i = 0; i < maps.size(); i++) {
                //复制图片后的模板行 这里暂时是两行
                for (int j = tagRowsIndex+1; j < templateTableRows.size() ; j+=2) {
                    List<XWPFTableRow> rows = new ArrayList<>();
                    XWPFTableRow newCreateRow1 = newCreateTable.createRow();
                    XWPFTableRow newCreateRow2 = newCreateTable.createRow();
                    CopyTableRow(newCreateRow1, templateTableRows.get(j));// 复制模板行文本和样式到新行
                    CopyTableRow(newCreateRow2, templateTableRows.get(j+1));// 复制模板行文本和样式到新行
                    rows.add(newCreateRow1);
                    rows.add(newCreateRow2);
                    replaceTableRows(rows, maps.get(i));// 处理标签替换
                }
            }

            //5.将重要图片替换上去
            String pic = "";
            Object significance = map.get("significance");
            if(significance.equals("A")){
                pic = absolutePath+importance_pic+"A.png";
            }else if(significance.equals("B")){
                pic = absolutePath+importance_pic+"B.png";
            }else{
                pic = absolutePath+importance_pic+"C.png";
            }
            XWPFParagraph xwpfParagraph = newCreateTable.getRow(3).getCell(5).getParagraphArray(0);
            xwpfParagraph = xwpfParagraph!=null?xwpfParagraph:newCreateTable.getRow(3).getCell(5).addParagraph();
            xwpfParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun xwpfRun = xwpfParagraph.createRun();
            xwpfRun.removeCarriageReturn();
            InputStream fileInputStream = new FileInputStream(pic);
            xwpfRun.addPicture(fileInputStream, Document.PICTURE_TYPE_PNG, null, Units.toEMU(30), Units.toEMU(30));
            fileInputStream.close();


            //6.替换到一开头的数据，和其他操作
            newCreateTable.removeRow(0);// 移除多出来的第一行
            XWPFParagraph paragraph = document.createParagraph();// 添加回车换行
            paragraph.setPageBreak(true);
            replaceTable(newCreateTable,map);//替换标签
        }


    }







    /**
     * 功能描述：在文档末尾生成段落
     * 开发人员： lyx
     * 创建时间： 2019/12/26 16:35
     * 参数：
     * 返回值：
     * 异常：
     */
    public void addParagraphInDocFooter(XWPFParagraph templateParagraph,
                                        List<Map<String, String>> list, Map<String, Object> parametersMap, int flag) {

        if (flag == 0) {
            XWPFParagraph createParagraph = document.createParagraph();
            // 设置段落样式
            createParagraph.getCTP().setPPr(templateParagraph.getCTP().getPPr());
            // 移除原始内容
            for (int pos = 0; pos < createParagraph.getRuns().size(); pos++) {
                createParagraph.removeRun(pos);
            }
            // 添加Run标签
            for (XWPFRun s : templateParagraph.getRuns()) {
                XWPFRun targetrun = createParagraph.createRun();
                CopyRun(targetrun, s);
            }

            replaceParagraph(createParagraph, parametersMap);

        } else if (flag == 1) {
            // 暂无实现
        }

    }





    /**
     * 功能描述：替换掉段落中的{}
     * 开发人员： lyx
     * 创建时间： 2019/12/26 16:35
     * 参数：
     * 返回值：
     * 异常：
     */
    public void replaceParagraph(XWPFParagraph xWPFParagraph, Map<String, Object> parametersMap) {
        List<XWPFRun> runs = xWPFParagraph.getRuns();
        String xWPFParagraphText = xWPFParagraph.getText();
        String regEx = "\\{.+?\\}";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(xWPFParagraphText);//正则匹配字符串{****}

        if (matcher.find()) {
            // 查找到有标签才执行替换
            int beginRunIndex = xWPFParagraph.searchText("{", new PositionInParagraph()).getBeginRun();// 标签开始run位置
            int endRunIndex = xWPFParagraph.searchText("}", new PositionInParagraph()).getEndRun();// 结束标签
            StringBuffer key = new StringBuffer();

            if (beginRunIndex == endRunIndex) {
                // {**}在一个run标签内
                XWPFRun beginRun = runs.get(beginRunIndex);
                String beginRunText = beginRun.text();

                int beginIndex = beginRunText.indexOf("{");
                int endIndex = beginRunText.indexOf("}");
                int length = beginRunText.length();

                if (beginIndex == 0 && endIndex == length - 1) {
                    // 该run标签只有{**}
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(beginRunIndex);
                    insertNewRun.getCTR().setRPr(beginRun.getCTR().getRPr());
                    // 设置文本
                    key.append(beginRunText.substring(1, endIndex));
                    insertNewRun.setText(getValueBykey(key.toString(),parametersMap));
                    xWPFParagraph.removeRun(beginRunIndex + 1);
                } else {
                    // 该run标签为**{**}** 或者 **{**} 或者{**}**，替换key后，还需要加上原始key前后的文本
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(beginRunIndex);
                    insertNewRun.getCTR().setRPr(beginRun.getCTR().getRPr());
                    // 设置文本
                    key.append(beginRunText.substring(beginRunText.indexOf("{")+1, beginRunText.indexOf("}")));
                    String textString=beginRunText.substring(0, beginIndex) + getValueBykey(key.toString(),parametersMap)
                            + beginRunText.substring(endIndex + 1);
                    insertNewRun.setText(textString);
                    xWPFParagraph.removeRun(beginRunIndex + 1);
                }

            }else {
                // {**}被分成多个run

                //先处理起始run标签,取得第一个{key}值
                XWPFRun beginRun = runs.get(beginRunIndex);
                String beginRunText = beginRun.text();
                int beginIndex = beginRunText.indexOf("{");
                if (beginRunText.length()>1  ) {
                    key.append(beginRunText.substring(beginIndex+1));
                }
                ArrayList<Integer> removeRunList = new ArrayList<>();//需要移除的run
                //处理中间的run
                for (int i = beginRunIndex + 1; i < endRunIndex; i++) {
                    XWPFRun run = runs.get(i);
                    String runText = run.text();
                    key.append(runText);
                    removeRunList.add(i);
                }

                // 获取endRun中的key值
                XWPFRun endRun = runs.get(endRunIndex);
                String endRunText = endRun.text();
                int endIndex = endRunText.indexOf("}");
                //run中**}或者**}**
                if (endRunText.length()>1 && endIndex!=0) {
                    key.append(endRunText.substring(0,endIndex));
                }



                //*******************************************************************
                //取得key值后替换标签

                //先处理开始标签
                if (beginRunText.length()==2 ) {
                    // run标签内文本{
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(beginRunIndex);
                    insertNewRun.getCTR().setRPr(beginRun.getCTR().getRPr());
                    // 设置文本
                    insertNewRun.setText(getValueBykey(key.toString(),parametersMap));
                    xWPFParagraph.removeRun(beginRunIndex + 1);//移除原始的run
                }else {
                    // 该run标签为**{**或者 {** ，替换key后，还需要加上原始key前的文本
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(beginRunIndex);
                    insertNewRun.getCTR().setRPr(beginRun.getCTR().getRPr());
                    // 设置文本
                    String textString=beginRunText.substring(0,beginRunText.indexOf("{"))+getValueBykey(key.toString(),parametersMap);
                    insertNewRun.setText(textString);
                    xWPFParagraph.removeRun(beginRunIndex + 1);//移除原始的run
                }

                //处理结束标签
                if (endRunText.length()==1 ) {
                    // run标签内文本只有}
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(endRunIndex);
                    insertNewRun.getCTR().setRPr(endRun.getCTR().getRPr());
                    // 设置文本
                    insertNewRun.setText("");
                    xWPFParagraph.removeRun(endRunIndex + 1);//移除原始的run

                }else {
                    // 该run标签为**}**或者 }** 或者**}，替换key后，还需要加上原始key后的文本
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(endRunIndex);
                    insertNewRun.getCTR().setRPr(endRun.getCTR().getRPr());
                    // 设置文本
                    String textString=endRunText.substring(endRunText.indexOf("}")+1);
                    insertNewRun.setText(textString);
                    xWPFParagraph.removeRun(endRunIndex + 1);//移除原始的run
                }

                //处理中间的run标签
                for (int i = 0; i < removeRunList.size(); i++) {
                    XWPFRun xWPFRun = runs.get(removeRunList.get(i));//原始run
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(removeRunList.get(i));
                    insertNewRun.getCTR().setRPr(xWPFRun.getCTR().getRPr());
                    insertNewRun.setText("");
                    xWPFParagraph.removeRun(removeRunList.get(i) + 1);//移除原始的run
                }

            }// 处理${**}被分成多个run

            replaceParagraph( xWPFParagraph, parametersMap);

        }//if 有标签

    }









    /**
     * 功能描述：复制表格行XWPFTableRow格式
     * 开发人员： lyx
     * 创建时间： 2019/12/26 16:36
     * 参数：
     * 返回值：
     * 异常：
     */
    private void CopyTableRow(XWPFTableRow target, XWPFTableRow source) {

        int tempRowCellsize = source.getTableCells().size();// 模板行的列数
        for (int i = 0; i < tempRowCellsize - 1; i++) {
            target.addNewTableCell();// 为新添加的行添加与模板表格对应行行相同个数的单元格
        }
        // 复制样式
        target.getCtRow().setTrPr(source.getCtRow().getTrPr());
        // 复制单元格
        for (int i = 0; i < target.getTableCells().size(); i++) {
            copyTableCell(target.getCell(i), source.getCell(i));
        }
    }






    /**
     * 功能描述：复制单元格XWPFTableCell格式
     * 开发人员： lyx
     * 创建时间： 2019/12/26 16:36
     * 参数：
     * 返回值：
     * 异常：
     */
    private void copyTableCell(XWPFTableCell newTableCell, XWPFTableCell templateTableCell) {
        // 列属性
        newTableCell.getCTTc().setTcPr(templateTableCell.getCTTc().getTcPr());
        // 删除目标 targetCell 所有文本段落
        for (int pos = 0; pos < newTableCell.getParagraphs().size(); pos++) {
            newTableCell.removeParagraph(pos);
        }
        // 添加新文本段落
        for (XWPFParagraph sp : templateTableCell.getParagraphs()) {
            XWPFParagraph targetP = newTableCell.addParagraph();
            copyParagraph(targetP, sp);
        }
    }


    /**
     * 功能描述：复制文本段落XWPFParagraph格式
     * 开发人员： lyx
     * 创建时间： 2019/12/26 16:37
     * 参数：
     * 返回值：
     * 异常：
     */
    private void copyParagraph(XWPFParagraph newParagraph, XWPFParagraph templateParagraph) {
        // 设置段落样式
        newParagraph.getCTP().setPPr(templateParagraph.getCTP().getPPr());
        // 添加Run标签
        for (int pos = 0; pos < newParagraph.getRuns().size(); pos++) {
            newParagraph.removeRun(pos);

        }
        for (XWPFRun s : templateParagraph.getRuns()) {
            XWPFRun targetrun = newParagraph.createRun();
            CopyRun(targetrun, s);
        }

    }


    /**
     * 功能描述：复制文本节点run
     * 开发人员： lyx
     * 创建时间： 2019/12/26 16:37
     * 参数：
     * 返回值：
     * 异常：
     */
    private void CopyRun(XWPFRun newRun, XWPFRun templateRun) {
        newRun.getCTR().setRPr(templateRun.getCTR().getRPr());
        // 设置文本
        newRun.setText(templateRun.text());


    }





    /**
     * 功能描述：根据参数parametersMap对表格的多行进行标签的替换
     * 开发人员： lyx
     * 创建时间： 2019/12/26 16:37
     * 参数：
     * 返回值：
     * 异常：
     */
    public void replaceTableRows( List<XWPFTableRow> tableRows, Map<String, Object> parametersMap) {

        for (XWPFTableRow xWPFTableRow : tableRows ) {
            List<XWPFTableCell> tableCells = xWPFTableRow.getTableCells();
            for (XWPFTableCell xWPFTableCell : tableCells ) {
                List<XWPFParagraph> paragraphs2 = xWPFTableCell.getParagraphs();
                for (XWPFParagraph xWPFParagraph : paragraphs2) {
                    replaceParagraph(xWPFParagraph, parametersMap);
                }
            }
        }

    }






    /**
     * 功能描述：根据map替换表格中的{}标签
     * 开发人员： lyx
     * 创建时间： 2019/12/26 16:38
     * 参数：
     * 返回值：
     * 异常：
     */
    public void replaceTable(XWPFTable xwpfTable,Map<String, Object> parametersMap){
        List<XWPFTableRow> rows = xwpfTable.getRows();
        for (XWPFTableRow xWPFTableRow : rows ) {
            List<XWPFTableCell> tableCells = xWPFTableRow.getTableCells();
            for (XWPFTableCell xWPFTableCell : tableCells ) {
                List<XWPFParagraph> paragraphs2 = xWPFTableCell.getParagraphs();
                for (XWPFParagraph xWPFParagraph : paragraphs2) {
                    replaceParagraph(xWPFParagraph, parametersMap);
                }
            }
        }

    }



    private String getValueBykey(String key, Map<String, Object> map) {
        String returnValue="";
        if (key != null) {
            try {
                returnValue=map.get(key)!=null ? map.get(key).toString() : "";
            } catch (Exception e) {
                // TODO: handle exception
                System.out.println("key:"+key+"***"+e);
                returnValue="";
            }

        }
        return returnValue;
    }

    //跨行跨列合并
    public void mergeCells(XWPFTable table, int fromrow, int endrow, int fromcell, int endcell) {
        //先合并行
        for (int rowindex = fromrow; rowindex <= endrow; rowindex++) {
            XWPFTableRow row = table.getRow(rowindex);
            if (rowindex == fromrow) {
                row.getCell(fromcell).getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
            } else {
                row.getCell(fromcell).getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
            }
        }
        //合并列
        for (int rowindex = fromrow; rowindex <= endrow; rowindex++) {
            for (int cellindex = fromcell; cellindex <= endcell; cellindex++) {
                XWPFTableCell cell = table.getRow(rowindex).getCell(cellindex);
                if (cellindex == fromcell) {
                    cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
                } else {
                    cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
                }
            }
        }
    }


    //跨列合并
    public void mergeCellsByCol(XWPFTable table, int rowindex, int fromcell, int endcell) {
        for (int cellindex = fromcell; cellindex <= endcell; cellindex++) {
            XWPFTableCell cell = table.getRow(rowindex).getCell(cellindex);
            if (cellindex == fromcell) {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
            } else {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
    }


    //跨行合并
    public void mergeCellByRow(XWPFTable table,  int cellindex, int fromrow, int endrow) {
        for (int rowindex = fromrow; rowindex <= endrow; rowindex++) {
            XWPFTableRow row = table.getRow(rowindex);
            if (rowindex == fromrow) {
                row.getCell(cellindex).getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
            } else {
                row.getCell(cellindex).getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
            }
        }
    }


    //图片放入word比例运算
    public List<Map<String,Object>> resetPic(List<ProjectDesignAlterationPic> filePaths) throws IOException {
        List<Map<String,Object>> mapList = new ArrayList<>();
        for (ProjectDesignAlterationPic filePath:filePaths) {
            Map<String,Object> map = new HashMap<>();
            File file = new File(absolutePath+filePath.getPic_url());
            //这里的这个用来区分是否是独占一行
            double type = 0;//默认一行显示一个
            BufferedImage read = ImageIO.read(file);
            double realityWidth = read.getWidth();
            double realityHeight = read.getHeight();
            int picType = read.getType();
            double widthHeightRatio = realityWidth/realityHeight;
            //1.判断长度
            double width = realityWidth;//720 360
            double height = realityHeight;//365

            while (height>345 || width>720){
                if(height>345){
                    height = 345;
                    width = 345*widthHeightRatio;
                }else if(width>720){
                    width = 720;
                    height = 720/widthHeightRatio;
                }
            }
            if (width<360){//表示这个图片单独占一行
                type = 1;
            }
            String picPath = filePath.getPic_url();
            map.put("fileName",filePath.getPic_name()==null?" ":filePath.getPic_name());
            map.put("filePath",absolutePath+picPath);
            map.put("width",width);
            map.put("height",height);
            map.put("picType",picType);
            map.put("type",type);
            mapList.add(map);
        }
        return mapList;

    }

    //给单元格插入图片方法
    void addPic( XWPFTableCell cell,Map<String,Object> nowCell) throws IOException, InvalidFormatException {
        //设置整体居中
        CTTc cttc = cell.getCTTc();
        CTTcPr ctPr = cttc.addNewTcPr();
        ctPr.addNewVAlign().setVal(STVerticalJc.CENTER);
        cttc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
        //插入图片
        XWPFParagraph xwpfParagraph = cell.getParagraphArray(0)!=null?cell.getParagraphArray(0):cell.addParagraph();
        //设置段落文本居中
        xwpfParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun xwpfParagraphRun = xwpfParagraph.createRun();

        InputStream  fileInputStream = new FileInputStream((String) nowCell.get("filePath"));

        xwpfParagraphRun.addPicture(fileInputStream,
                (int)nowCell.get("picType"),
                (String) nowCell.get("fileName"),
//                 Units.toEMU(500), Units.toEMU(700));
                Units.toEMU((Double) nowCell.get("width")), Units.toEMU((Double) nowCell.get("height")));
        xwpfParagraphRun.addBreak();
        //设置图片名
        String fileName = (String)nowCell.get("fileName");
        xwpfParagraphRun.setText(StringUtils.isEmpty(fileName)?"未命名":fileName);
        fileInputStream.close();
    }


    /**
     * 功能描述：创建目录
     * 开发人员： lyx
     * 创建时间： 2019/12/27 10:06
     * 参数：
     * 返回值：
     * 异常：
     */
    public  void generateTOC(String filePath) throws InvalidFormatException, FileNotFoundException, IOException {
        FileInputStream mulu = new FileInputStream(filePath);
        XWPFDocument document = new XWPFDocument(mulu);
        String findText = "toc";
        String replaceText = "";
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (int i = 0; i <paragraphs.size() ; i++) {
            for (XWPFRun r : paragraphs.get(i).getRuns()) {
                int pos = r.getTextPosition();
                String text = r.getText(pos);
                if (text != null && text.contains(findText)) {
                    text = text.replace(findText, replaceText);
                    r.setText(text, 0);
                    addField(paragraphs.get(i), "TOC \\o \"1-3\" \\h \\z \\u");
//                    addField(p, "TOC \\h");
                    break;
                }

            }

        }
        OutputStream out = new FileOutputStream(filePath);
        document.write(out);
        out.close();
    }

    private static void addField(XWPFParagraph paragraph, String fieldName) {
        CTSimpleField ctSimpleField = paragraph.getCTP().addNewFldSimple();
        ctSimpleField.setInstr(fieldName);
        ctSimpleField.setDirty(STOnOff.TRUE);
        ctSimpleField.addNewR().addNewT().setStringValue("<<fieldName>>");
    }
}



