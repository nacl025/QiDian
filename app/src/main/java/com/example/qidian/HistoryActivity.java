package com.example.qidian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<String> historyList = new ArrayList<String>();
    private ListView listView ;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Intent intent = getIntent();

        int type = intent.getIntExtra("equationType",1);
        historyList.clear();

        listView = findViewById(R.id.lv_history);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, historyList);
        listView.setAdapter(adapter);

        findViewById(R.id.btn_history1).setOnClickListener(this);
        findViewById(R.id.btn_history2).setOnClickListener(this);
        findViewById(R.id.btn_history3).setOnClickListener(this);

        queryHistory(type);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_history1:
                queryHistory(1);
                break;
            case R.id.btn_history2:
                queryHistory(2);
                break;
            case R.id.btn_history3:
                queryHistory(3);
                break;
        }
    }

    /**
     *
     * @param type 1:双数加减单数
     *             2：双数加减双数
     *             3: 乘法
     */
    private void queryHistory(int type){
        historyList.clear();
        adapter.notifyDataSetChanged();
        try {
            MyDBHelper dbHelper = new MyDBHelper(this, null);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //参数1：表名
            // 参数2：要想显示的列
            //参数3：where子句
            //参数4：where子句对应的条件值
            //参数5：分组方式
            //参数6：having条件
            //参数7：排序方式
           Cursor cursor = db.query("history_table",
                    new String[]{"datetime", "score", "errors"},
                    "type=?",
                    new String[]{type+""},
                    null,
                    null,
                    "errors asc, score asc",
                    null);
           int a = 0;
            while (cursor.moveToNext()) {
                a++;
                String datetime = cursor.getString(cursor.getColumnIndex("datetime"));
                int score = cursor.getInt(cursor.getColumnIndex("score"));
                int minutes = score / 60;
                int second = score % 60;
                int errors = cursor.getInt(cursor.getColumnIndex("errors"));
                historyList.add(a + "  " +datetime + "    用时" + minutes + "分" + second + "秒,错误" + errors);
            }
            //关闭数据库
            db.close();
        }catch (Exception ex){
            Log.e("HistoryActivity", ex.getMessage());
        }
    }

    /**
     *
     */
    private void deleteItemTemp(){
        try {
            MyDBHelper dbHelper = new MyDBHelper(this, null);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int t = db.delete("history_table","errors>25",null);
            Log.d("HistoryActivity", "t=" +t);
            //关闭数据库
            db.close();
        }catch (Exception ex){
            Log.e("HistoryActivity", ex.getMessage());
        }

    }

}
