package com.example.qidian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "MainActivity";
    private ArrayList<Equation> arrayList = new ArrayList<Equation>();
    private ArrayList<Equation> resultList = new ArrayList<Equation>();
    private TextView tv_a, tv_b, tv_c, tv_show;
    private EditText et_result, et_result2;
    private Button btn_next;
    private ListView listView;
    private LinearLayout layout_yu;
    private int currtnIdex;
    EquationAdapter adapter;
    Date beginTime, endTime;
    private int equationType = 1;//1双数加减单数;2双数加减双数;3乘法;4除法


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intView();
        findViewById(R.id.btn_100).setOnClickListener(this);
        findViewById(R.id.btn_reset).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        findViewById(R.id.btn_history).setOnClickListener(this);
        findViewById(R.id.btn_shuang).setOnClickListener(this);
        findViewById(R.id.btn_screenshot).setOnClickListener(this);
        findViewById(R.id.btn_cheng).setOnClickListener(this);
        findViewById(R.id.btn_div).setOnClickListener(this);
        findViewById(R.id.btn_excel).setOnClickListener(this);
        verifyStoragePermissions(this);

    }

    private void intView() {
        tv_a = findViewById(R.id.tv_a);
        tv_b = findViewById(R.id.tv_b);
        tv_c = findViewById(R.id.tv_c);
        tv_show = findViewById(R.id.tv_show);
        et_result = findViewById(R.id.et_result);
        btn_next = findViewById(R.id.btn_next);
        listView = findViewById(R.id.lv_note);
        layout_yu = findViewById(R.id.layout_yu);
        et_result2 = findViewById(R.id.et_result2);

        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, errorList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_100:
                dealBtn1();
                break;
            case R.id.btn_reset:
                reset();
                break;
            case R.id.btn_next:
                dealNext();
                break;
            case R.id.btn_shuang:
                dealBtnShuang();
                break;
            case R.id.btn_history:
                Intent it = new Intent(MainActivity.this, HistoryActivity.class);
                it.putExtra("equationType", equationType);
                startActivity(it);
                break;
            case R.id.btn_screenshot:
                takeScreenshot();
                break;
            case R.id.btn_cheng:
                dealBtnCheng();
                break;
            case R.id.btn_div:
                dealBtnChu();
                break;
            case R.id.btn_excel:
                export();
                break;

        }
    }

    private void dealBtnChu(){
        reset();
        equationType=4;
        initList(equationType,100);
        setTextView();
        beginTime = new Date(System.currentTimeMillis());

    }

    private void dealBtnCheng() {
        reset();
        equationType = 3;
        initList(equationType, 100);
        setTextView();
        beginTime = new Date(System.currentTimeMillis());

    }

    /**
     *
     */
    private void dealBtn1() {
        reset();
        equationType = 1;
        initList(equationType,100);
        setTextView();
        beginTime = new Date(System.currentTimeMillis());
    }

    private void dealBtnShuang() {
        reset();
        equationType = 2;
        initList(equationType, 100);
        setTextView();
        beginTime = new Date(System.currentTimeMillis());
    }


    private void reset() {
        arrayList.clear();
        resultList.clear();
        currtnIdex = 0;
        clearTextView();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        tv_show.setVisibility(View.GONE);
        btn_next.setEnabled(true);
    }

    private void dealNext() {
        if (et_result.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入计算结果", Toast.LENGTH_LONG).show();
            return;
        }
        if (currtnIdex < arrayList.size() - 2) {
            saveResult();
            currtnIdex++;
            setTextView();
        } else if (currtnIdex == arrayList.size() - 2) {
            saveResult();
            currtnIdex++;
            setTextView();
            btn_next.setText("完成");
        } else if (currtnIdex == arrayList.size() - 1) {
            saveResult();
            printResult();
            btn_next.setEnabled(false);
        }

        et_result.requestFocus();
        et_result.requestFocusFromTouch();
    }

    private void printResult() {
        endTime = new Date(System.currentTimeMillis());
        long diff = endTime.getTime() - beginTime.getTime();
        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        long second = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000;
        adapter = new EquationAdapter(this, resultList);
        listView.setAdapter(adapter);
        DateFormat dateFormat = new SimpleDateFormat(
                "MM月dd日");
        String datetime = dateFormat.format(endTime);
        if (adapter.getErrorCount() == 0) {
            tv_show.setText( datetime + ":用时" + minutes + "分" + second + "秒, 全部正确！");
            tv_show.setVisibility(View.VISIBLE);
        } else {
            tv_show.setText(datetime + ":用时" + minutes + "分" + second + "秒, " + adapter.getErrorCount() + "道错误。");
            tv_show.setVisibility(View.VISIBLE);
        }
        int score = (int) (minutes * 60 + second);
        saveDB(endTime, score, adapter.getErrorCount());
    }

    private void saveResult() {
        if(equationType == 4){
            int r1 = Integer.parseInt(et_result.getText().toString());
            int r2 = et_result2.getText().toString().isEmpty()? 0 : Integer.parseInt(et_result2.getText().toString());
            if(r1 == arrayList.get(currtnIdex).getChuResult().ChuShu && r2 == arrayList.get(currtnIdex).getChuResult().YuShu){
                arrayList.get(currtnIdex).setIsSuccess(true);
            }else{
                arrayList.get(currtnIdex).setIsSuccess(false);
            }
            arrayList.get(currtnIdex).setTmpChuFaResult(r1, r2);
            resultList.add(arrayList.get(currtnIdex));
        }else {
            int r = Integer.parseInt(et_result.getText().toString());
            if (r != arrayList.get(currtnIdex).getResult()) {
                arrayList.get(currtnIdex).setIsSuccess(false);
            } else {
                arrayList.get(currtnIdex).setIsSuccess(true);
            }
            arrayList.get(currtnIdex).setTmpResult(r);
            resultList.add(arrayList.get(currtnIdex));
        }
    }

    /**
     * 历史记录保存到本地库
     *
     * @param date
     * @param score
     * @param errors
     */
    private void saveDB(Date date, int score, int errors) {
        DateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String datetime = dateFormat.format(date);

        try {
            MyDBHelper dbHelper = new MyDBHelper(this, null);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //生成ContentValues对象 //key:列名，value:想插入的值
            ContentValues cv = new ContentValues();
            //往ContentValues对象存放数据，键-值对模式
            cv.put("datetime", datetime);
            cv.put("score", score);
            cv.put("type", equationType);
            cv.put("errors", errors);
            cv.put("count", 100);
            //调用insert方法，将数据插入数据库
            db.insert("history_table", null, cv);
            //关闭数据库
            db.close();
        } catch (Exception ex) {
            Log.e("MainAcitiy", ex.getMessage());
        }
    }


    private void setTextView() {
        tv_a.setText(String.valueOf(arrayList.get(currtnIdex).getA()));
        tv_b.setText(String.valueOf(arrayList.get(currtnIdex).getB()));
        et_result.setText("");
        et_result2.setText("");
        switch (arrayList.get(currtnIdex).getSign()) {
            case 0:
                tv_c.setText("+");
                layout_yu.setVisibility(View.GONE);
                break;
            case 1:
                tv_c.setText("-");
                layout_yu.setVisibility(View.GONE);
                break;
            case 2:
                tv_c.setText("×");
                layout_yu.setVisibility(View.GONE);
                break;
            case 3:
                tv_c.setText("÷");
                layout_yu.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void clearTextView() {
        tv_a.setText("");
        tv_b.setText("");
        tv_c.setText("");
        et_result.setText("");
        et_result2.setText("");
        btn_next.setText("下一个");
    }

    /**
     * @param type 1: 双数加减单数
     *             2：双数加减双数
     *             3: 乘法
     */
    private void initList(int type, int count) {
        for (int i = 0; i < 60; i++) {
            Equation e;
            while (true) {
                e = getEquation(type);
                if (type == 1) {//判断是否进位加减
                    if (e.getSign() == 1) {
                        if (e.getA() % 10 < e.getB()) {
                            break;
                        }
                    } else if (e.getSign() == 0) {
                        if ((e.getA() % 10 + e.getB()) > 10) {
                            break;
                        }
                    }
                } else if (type == 2) {//判断是否进位加减
                    if (e.getSign() == 1) {
                        if (e.getA() % 10 < e.getB() % 10) {
                            break;
                        }
                    } else if (e.getSign() == 0) {
                        if ((e.getA() % 10 + e.getB() % 10) > 10) {
                            break;
                        }
                    }
                } else if(type == 4) {
                    if(e.getChuResult().ChuShu < 9){
                        break;
                    }
                } else{
                    break;
                }
            }
            arrayList.add(e);
        }
    }

    /**
     * 生成双数加减单数算式
     *
     * @param type 1: 双数加减单数
     *             2：双数加减双数
     * @return
     */
    private Equation getEquation(int type) {
        int a;
        int b;
        int c;
        if (type == 1) {
            a = getRandomNum1();
            b = getRandomNum2();
            c = (int) (Math.random() * 20) % 2;
        } else if (type == 2) {
            a = getRandomNum1();
            b = getRandomNum3();
            c = (int) (Math.random() * 20) % 2;
        } else if(type == 3){
            a = getRandomNum4();
            b = getRandomNum4();
            c = 2;
        } else {
            a = getRandomNum5();
            b = getRandomNum4();
            c = 3;
        }
        Equation equation = new Equation(a, b, c);
        if(type == 1 || type ==2) {
            if (equation.getResult() >= 100 || equation.getResult() <= 0) {
                return getEquation(type);
            } else {
                return equation;
            }
        }else{
            return equation;
        }
    }


    /**
     * 生成20<n<100的数
     *
     * @return
     */
    private int getRandomNum1() {
        int a;
        while (true) {
            a = (int) (Math.random() * 100);
            if (a > 20 && a < 100) {
                break;
            }
        }
        return a;
    }

    /**
     * 生成4<n<10的数
     *
     * @return
     */
    private int getRandomNum2() {
        int b;
        while (true) {
            b = (int) (Math.random() * 20);
            if (b > 4 && b < 10) {
                break;
            }
        }
        return b;
    }

    /**
     * 生成10<n<50的数
     *
     * @return
     */
    private int getRandomNum3() {
        int t;
        while (true) {
            t = (int) (Math.random() * 60);
            if (t > 10 && t < 50) {
                break;
            }
        }
        return t;
    }

    /**
     * 生成1<n<10的数
     *
     * @return
     */
    private int getRandomNum4() {
        int b;
        while (true) {
            b = (int) (Math.random() * 20);
            if (b > 1 && b < 10) {
                break;
            }
        }
        return b;
    }

    /**
     * 生成10<n<90的数
     *
     * @return
     */
    private int getRandomNum5(){
        int a;
        while (true) {
            a = (int) (Math.random() * 100);
            if (a > 10 && a < 90) {
                break;
            }
        }
        return a;
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void takeScreenshot() {
        new MyAsyncTask().execute();
    }

    class MyAsyncTask extends AsyncTask{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Thread.sleep(1000);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            View dView = getWindow().getDecorView();
            dView.setDrawingCacheEnabled(true);
            dView.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
            if (bitmap != null) {
                try {
                    // 获取内置SD卡路径
                    String sdCardPath = Environment.getExternalStorageDirectory().getPath();
                    // 图片文件路径
                    DateFormat dateFormat = new SimpleDateFormat(
                            "yyyyMMddHHmmss");
                    String time = dateFormat.format(new Date(System.currentTimeMillis()));
                    String fileName = time + ".jpg";
                    String filePath = sdCardPath + File.separator + "DCIM" + File.separator + "Screenshots" + File.separator + fileName;
                    File file = new File(filePath);
                    FileOutputStream os = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    os.flush();
                    os.close();
                    Log.d(TAG, "存储完成");
                    //把文件插入到系统图库
                /*MediaStore.Images.Media.insertImage(getContentResolver(),
                        file.getAbsolutePath(), fileName, null);*/
                    // 最后通知图库更新
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Toast.makeText(getApplicationContext(), "已保存截屏", Toast.LENGTH_LONG).show();
        }

    }


    private String excelFilePath = "";
    private String[] colNames = new String[]{"姓名","时间"};
    //"电话号码","日期", "时间", "体温", "特殊情况", "地理位置"
    String[] pess = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    /**
     * 导出表格的操作
     * "新的运行时权限机制"只在应用程序的targetSdkVersion>=23时生效，并且只在6.0系统之上有这种机制，在低于6.0的系统上应用程序和以前一样不受影响。
     * 当前应用程序的targetSdkVersion小于23（为22），系统会默认其尚未适配新的运行时权限机制，安装后将和以前一样不受影响：即用户在安装应用程序的时候默认允许所有被申明的权限
     */
    private void export() {
        if (this.getApplicationInfo().targetSdkVersion >= 23 && Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        } else {
            writeExcel();
        }
    }


    /**
     * 动态请求读写权限
     */
    private void requestPermission() {
        if (!checkPermission()) {//如果没有权限则请求权限再写
            ActivityCompat.requestPermissions(this, pess, 100);
        } else {//如果有权限则直接写
            writeExcel();
        }
    }


    /**
     * 检测权限
     *
     * @return
     */
    private boolean checkPermission() {
        for (String permission : pess) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 将数据写入excel表格
     */
    private void writeExcel() {
        if (getExternalStoragePath() == null) return;
        File file = new File(getExternalStoragePath() + "/Joey");
        makeDir(file);
        DateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd-HH-mm-ss");
        String fileName = dateFormat.format(System.currentTimeMillis()) + ".xls";
        excelFilePath = getExternalStoragePath() + "/Joey/" + fileName;
        if (checkFile(excelFilePath)) {
            deleteByPath(excelFilePath);//如果文件存在则先删除原有的文件
        }
        ExcelUtils.initExcel(excelFilePath, "First", "Second", colNames);//需要写入权限
        ExcelUtils.writeObjListToExcel(getEquationData(), getResultData(), excelFilePath, this);
      }

    /**
     * 根据路径生成文件夹
     *
     * @param filePath
     */
    public static void makeDir(File filePath) {
        if (!filePath.getParentFile().exists()) {
            makeDir(filePath.getParentFile());
        }
        filePath.mkdir();
    }

    /**
     * 获取外部存储路径
     *
     * @return
     */
    public String getExternalStoragePath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
            return sdDir.toString();
        } else {
            Toast.makeText(this, "找不到外部存储路径，读写手机存储权限被禁止，请在权限管理中心手动打开权限", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /**
     *
     *
     * @return
     */
    public ArrayList<ArrayList<String>> getEquationData() {
        ArrayList<ArrayList<String>> datas = new ArrayList<>();
        ArrayList<String> data = null;
        for (int i = 0; i < 30; i++) {
            data = new ArrayList<>();
            data.clear();
            for (int j = 0; j < 2; j++) {
                Equation e = arrayList.get(i * 2 + j);
                String a = "";
                switch (e.getSign()) {
                    case 0:
                        a = "+";
                        break;
                    case 1:
                        a = "-";
                        break;
                    case 2:
                        a = "×";
                        break;
                    case 3:
                        a = "÷";
                        break;
                }
                String s = e.getA() + a + e.getB() + "=";
                data.add(s);
            }
            datas.add(data);
        }
        return datas;
    }

    public ArrayList<ArrayList<String>> getResultData(){
        ArrayList<ArrayList<String>> datas = new ArrayList<>();
        ArrayList<String> data = null;
        for (int i = 0; i < 30; i++) {
            data = new ArrayList<>();
            data.clear();
            for (int j = 0; j < 2; j++) {
                Equation e = arrayList.get(i * 2 + j);
                String s ="";
                if(e.getSign() ==3 ){
                    s = e.getChuResult().ChuShu +"......" + e.getChuResult().YuShu;
                }else{
                    s = e.getResult() + "";
                }
                data.add(s);
            }
            datas.add(data);
        }
        return datas;
    }

    /**
     * 根据文件路径检测文件是否存在,需要读取权限
     *
     * @param filePath 文件路径
     * @return true存在
     */
    private boolean checkFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) return true;
            else return false;
        } else {
            return false;
        }
    }


    /**
     * 根据文件路径删除文件
     *
     * @param filePath
     */
    private void deleteByPath(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile())
                file.delete();
        }
    }
}
