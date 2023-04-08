package com.example.qidian;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;



/**
 *
 */
public class ExcelUtils {

    public static WritableFont arial12font = null;
    public static WritableCellFormat arial12format = null;

    public final static String UTF8_ENCODING = "UTF-8";
    public final static String GBK_ENCODING = "GBK";


    public static void format() {
        try {
            arial12font = new WritableFont(WritableFont.ARIAL, 19);
            arial12format = new WritableCellFormat(arial12font);
            arial12format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化表格，包括文件名、sheet名、各列的名字
     *
     * @param filePath  文件路径
     * @param sheetName sheet名
     * @param colName   各列的名字
     */
    public static void initExcel(String filePath, String sheetName, String[] colName) {
        format();
        WritableWorkbook workbook = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet(sheetName, 0);
            sheet.getSettings().setDefaultColumnWidth(25);
            sheet.addCell((WritableCell) new Label(0, 0, filePath, arial12format));
            for (int col = 0; col < colName.length; col++) {
                sheet.addCell(new Label(col, 0, colName[col], arial12format));
            }
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 初始化表格，包括文件名、sheet名、各列的名字
     *
     * @param filePath  文件路径
     * @param sheetName sheet名
     * @param sheet2Name  sheet2名
     * @param colName   各列的名字
     */
    public static void initExcel(String filePath, String sheetName,String sheet2Name, String[] colName) {
        format();
        WritableWorkbook workbook = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet(sheetName, 0);
            sheet.getSettings().setDefaultColumnWidth(40);
            sheet.addCell((WritableCell) new Label(0, 0, filePath, arial12format));
            for (int col = 0; col < colName.length; col++) {
                sheet.addCell(new Label(col, 0, colName[col], arial12format));
            }
            //*****************************************************************
            WritableSheet sheet2 = workbook.createSheet(sheet2Name, 1);
            sheet2.getSettings().setDefaultColumnWidth(40);
            sheet2.addCell((WritableCell) new Label(0, 0, filePath, arial12format));
            for (int col = 0; col < colName.length; col++) {
                sheet2.addCell(new Label(col, 0, colName[col], arial12format));
            }

            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 将数据写入Excel表格
     *
     * @param objList  要写的列表数据
     * @param filePath 文件路径
     * @param c        上下文
     * @param <T>
     */
    public static <T> void writeObjListToExcel(List<T> objList, String filePath, Context c) {
        if (objList != null && objList.size() > 0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                in = new FileInputStream(new File(filePath));
                Workbook workbook = Workbook.getWorkbook(in);
                writebook = Workbook.createWorkbook(new File(filePath), workbook);
                WritableSheet sheet = writebook.getSheet(0);
                for (int j = 0; j < objList.size(); j++) {
                    ArrayList<String> list = (ArrayList<String>) objList.get(j);
                    for (int i = 0; i < list.size(); i++) {
                        sheet.addCell(new Label(i, j + 1, list.get(i), arial12format));
                    }
                }
                writebook.write();
                Toast.makeText(c, " 导出成功 ", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * 将数据写入Excel表格
     *
     * @param objList1  要写的列表数据
     * @param objList2  Sheet2数据
     * @param filePath 文件路径
     * @param c        上下文
     * @param <T>
     */
    public static <T> void writeObjListToExcel(List<T> objList1, List<T> objList2, String filePath, Context c) {
        if (objList1 != null && objList1.size() > 0 && objList2 != null && objList2.size()>0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                in = new FileInputStream(new File(filePath));
                Workbook workbook = Workbook.getWorkbook(in);
                writebook = Workbook.createWorkbook(new File(filePath), workbook);
                WritableSheet sheet1 = writebook.getSheet(0);
                for (int j = 0; j < objList1.size(); j++) {
                    ArrayList<String> list = (ArrayList<String>) objList1.get(j);
                    for (int i = 0; i < list.size(); i++) {
                        sheet1.addCell(new Label(i, j + 1, list.get(i), arial12format));
                    }
                }
                WritableSheet sheet2 = writebook.getSheet(1);
                for (int j = 0; j < objList1.size(); j++) {
                    ArrayList<String> list = (ArrayList<String>) objList2.get(j);
                    for (int i = 0; i < list.size(); i++) {
                        sheet2.addCell(new Label(i, j + 1, list.get(i), arial12format));
                    }
                }
                writebook.write();
                Toast.makeText(c, " 导出成功 ", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    void test(){
        try {
            //创建Excel工作簿;
            WritableWorkbook workbook = Workbook.createWorkbook(new File("C:/ExcelDemo.xls"));

            //创建Excel电子薄;
            WritableSheet sheet = workbook.createSheet("第一个Sheet", 0);
            //分别给2,3,4列设置不同的宽度;
            sheet.setColumnView(1, 40);
            sheet.setColumnView(1, 30);
            sheet.setColumnView(2, 50);
            sheet.setColumnView(3, 20);

            //给sheet电子版中所有的列设置默认的列的宽度;
            sheet.getSettings().setDefaultColumnWidth(30);

            //设置字体;
            WritableFont font1 = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);

            WritableCellFormat cellFormat1 = new WritableCellFormat(font1);
            //设置背景颜色;
            cellFormat1.setBackground(Colour.BLUE_GREY);
            //设置边框;
            cellFormat1.setBorder(Border.ALL, BorderLineStyle.DASH_DOT);
            //设置自动换行;
            cellFormat1.setWrap(true);
            //设置文字居中对齐方式;
            cellFormat1.setAlignment(Alignment.CENTRE);
            //设置垂直居中;
            cellFormat1.setVerticalAlignment(VerticalAlignment.CENTRE);
            //创建单元格
            Label label1 = new Label(0, 0, "第一行第一个单元格(测试是否自动换行!)", cellFormat1);
            Label label2 = new Label(1, 0, "第一行第二个单元格", cellFormat1);
            Label label3 = new Label(2, 0, "第一行第三个单元格", cellFormat1);
            Label label4 = new Label(3, 0, "第一行第四个单元格", cellFormat1);
            //添加到行中;
            sheet.addCell(label1);
            sheet.addCell(label2);
            sheet.addCell(label3);
            sheet.addCell(label4);

            //给第二行设置背景、字体颜色、对齐方式等等;
            WritableFont font2 = new WritableFont(WritableFont.ARIAL, 14, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLUE2);
            WritableCellFormat cellFormat2 = new WritableCellFormat(font2);
            cellFormat2.setAlignment(Alignment.CENTRE);
            cellFormat2.setBackground(Colour.PINK);
            cellFormat2.setBorder(Border.ALL, BorderLineStyle.THIN);
            cellFormat2.setWrap(true);

            //创建单元格;
            Label label11 = new Label(0, 1, "第二行第一个单元格(测试是否自动换行!)", cellFormat2);
            Label label22 = new Label(1, 1, "第二行第二个单元格", cellFormat2);
            Label label33 = new Label(2, 1, "第二行第三个单元格", cellFormat2);
            Label label44 = new Label(3, 1, "第二行第四个单元格", cellFormat2);

            sheet.addCell(label11);
            sheet.addCell(label22);
            sheet.addCell(label33);
            sheet.addCell(label44);

            //写入Excel表格中;
            workbook.write();
            //关闭流;
            workbook.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
