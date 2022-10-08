package com.example.qidian;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtils {
    private static String path1 = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static String path2 = Environment.getDownloadCacheDirectory().getAbsolutePath();
    private static String pathExt = "/111/222/333/444/555/";
    private static String fileName = "history.txt";

    public static void write(String str) {
        String filePath = null;
        boolean hasSDCard =Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            filePath = path1 + pathExt + fileName;
        } else {
            filePath = path2 + pathExt + fileName;
        }
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();//生成文件外层的文件夹
                file.createNewFile();//生成文件
            }
            FileOutputStream os = new FileOutputStream(file);
            os.write(str.getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String read() {
        String content = "";
        String filePath;

        boolean sdcard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdcard) {
            filePath = path1 + pathExt + fileName;
        } else {
            filePath = path2 + pathExt + fileName;
        }
        try {
            File file = new File(filePath);
            if (file.exists()) {
                FileInputStream is = new FileInputStream(file);
                InputStreamReader inputReader = new InputStreamReader(is);//设置流读取方式
                BufferedReader buffReader = new BufferedReader(inputReader);
                String line;
                try {
                    while (null != (line = buffReader.readLine())) {
                        content += line + "\n";//读取的文件容
                    }
                    is.close();//关闭输入流
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != is) {
                            is.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return content;
    }
}
