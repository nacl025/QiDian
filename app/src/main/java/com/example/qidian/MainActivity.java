package com.example.qidian;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "MainActivity";
    private ArrayList<Equation> arrayList = new ArrayList<Equation>();
    private ArrayList<Equation> resultList = new ArrayList<Equation>();
    private TextView tv_a, tv_b, tv_c, tv_show;
    private EditText et_result;
    private Button btn_next;
    private ListView listView;
    private int currtnIdex;
    EquationAdapter adapter;
    Date beginTime, endTime;
    private int equationType = 1;//1双数加减单数;2双数加减双数;3乘法


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intView();
        findViewById(R.id.btn_100).setOnClickListener(this);
        findViewById(R.id.btn_reset).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        findViewById(R.id.btn_history).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn_screenshot).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
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
            case R.id.btn2:
                dealBtn2();
                break;
            case R.id.btn_history:
                Intent it = new Intent(MainActivity.this, HistoryActivity.class);
                it.putExtra("equationType", equationType);
                startActivity(it);
                break;
            case R.id.btn_screenshot:
                takeScreenshot();
                break;
            case R.id.btn3:
                dealBtn3();
                break;

        }
    }

    private void dealBtn3() {
        reset();
        equationType = 3;
        initList(equationType);
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
        int r = Integer.parseInt(et_result.getText().toString());
        if (r != arrayList.get(currtnIdex).getResult()) {
            arrayList.get(currtnIdex).setIsSuccess(false);
        } else {
            arrayList.get(currtnIdex).setIsSuccess(true);
        }
        arrayList.get(currtnIdex).setTmpResult(r);
        resultList.add(arrayList.get(currtnIdex));
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
            //调用insert方法，将数据插入数据库
            db.insert("history_table", null, cv);
            //关闭数据库
            db.close();
        } catch (Exception ex) {
            Log.e("MainAcitiy", ex.getMessage());
        }
    }

    /**
     *
     */
    private void dealBtn1() {
        reset();
        equationType = 1;
        initList(equationType);
        setTextView();
        beginTime = new Date(System.currentTimeMillis());
    }

    private void dealBtn2() {
        reset();
        equationType = 2;
        initList(equationType);
        setTextView();
        beginTime = new Date(System.currentTimeMillis());
    }

    private void setTextView() {
        tv_a.setText(String.valueOf(arrayList.get(currtnIdex).getA()));
        tv_b.setText(String.valueOf(arrayList.get(currtnIdex).getB()));
        et_result.setText("");
        switch (arrayList.get(currtnIdex).getSign()) {
            case 0:
                tv_c.setText("+");
                break;
            case 1:
                tv_c.setText("-");
                break;
            case 2:
                tv_c.setText("×");
                break;
            case 3:
                tv_c.setText("÷");
                break;
        }
    }

    private void clearTextView() {
        tv_a.setText("");
        tv_b.setText("");
        tv_c.setText("");
        et_result.setText("");
        btn_next.setText("下一个");
    }

    /**
     * @param type 1: 双数加减单数
     *             2：双数加减双数
     *             3: 乘法
     */
    private void initList(int type) {
        for (int i = 0; i < 60; i++) {
            Equation e;
            while (true) {
                e = getEquation(type);
                if (type == 1) {
                    if (e.getSign() == 1) {
                        if (e.getA() % 10 < e.getB()) {
                            break;
                        }
                    } else if (e.getSign() == 0) {
                        if ((e.getA() % 10 + e.getB()) > 10) {
                            break;
                        }
                    }
                } else if (type == 2) {
                    if (e.getSign() == 1) {
                        if (e.getA() % 10 < e.getB() % 10) {
                            break;
                        }
                    } else if (e.getSign() == 0) {
                        if ((e.getA() % 10 + e.getB() % 10) > 10) {
                            break;
                        }
                    }
                } else if (type == 3) {
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
        } else {
            a = getRandomNum4();
            b = getRandomNum4();
            c = 2;
        }
        Equation equation = new Equation(a, b, c);
        if (equation.getResult() >= 100 || equation.getResult() <= 0) {
            return getEquation(type);
        } else {
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


    ArrayList<String> permissionList = new ArrayList<>();
    private String[] permissions = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    /**
     * 申请系统权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permissions[0]);
            }
            if (checkSelfPermission(permissions[1]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permissions[1]);
            }

            if (!permissionList.isEmpty()) {
                String[] permissions1 = permissionList.toArray(new String[permissionList.size()]);
                requestPermissions(permissions1, 1);
            }
        }
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

}
