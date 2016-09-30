package com.wustwxy2.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wustwxy2.R;
import com.wustwxy2.models.JwInfoDB;
import com.wustwxy2.util.Ksoap2;
import com.wustwxy2.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class SetCourseActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private SystemBarTintManager tintManager;
    private RelativeLayout titleRl;
    private Button btBack;
    private TextView tvTitle;
    private LinearLayout llZc;
    private Button btSetZc;
    private LinearLayout llXq;
    private Button btSetXq;
    private PopupWindow mPopupWindow;
    private Button btSave;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private String selectXq;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String kb = msg.obj.toString();
            if (!kb.equals("anyType{}")) {
                Log.d("MainActivity",kb);
                Utility.handleCourses(JwInfoDB.getJwInfoDB(SetCourseActivity.this),kb);
                mEditor.putString("kbData",kb);
                mEditor.putString("xq",selectXq);
                mEditor.commit();
                SearchTableActivity.CourseActivity.finish();
                btSetXq.setText("当前学期  "+selectXq);
                Toast.makeText(SetCourseActivity.this,"已修改学期", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(SetCourseActivity.this, R.string.no_course_data_toast , Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_course);
        initWindow();
        titleRl= (RelativeLayout) findViewById(R.id.title_rl);
        btBack=(Button)findViewById(R.id.bt_back);
        tvTitle=(TextView)findViewById(R.id.tv_title);
        llZc=(LinearLayout)findViewById(R.id.ll_zc);
        llXq=(LinearLayout)findViewById(R.id.ll_xq);
        btSave=(Button)findViewById(R.id.bt_save);
        mPreferences = getSharedPreferences("data", MODE_PRIVATE);
        mEditor = mPreferences.edit();

        btBack.setOnClickListener(this);
        if(getIntent().getIntExtra("XqOrZc",0)==1) {
            llXq.setVisibility(View.GONE);
            btSetZc = (Button) findViewById(R.id.bt_set_zc);
            btSetZc.setText("当前周次  第"+mPreferences.getInt("currentZc",1)+"周");
            btSetZc.setOnClickListener(this);
            btSave.setVisibility(View.GONE);
            btSave.setOnClickListener(this);
        }else {
            tvTitle.setText("选择学期");
            llZc.setVisibility(View.GONE);
            btSetXq=(Button)findViewById(R.id.bt_set_xq);
            btSetXq.setText("当前学期  "+mPreferences.getString("xq",""));
            btSetXq.setOnClickListener(this);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_back:
                finish();
                if(SearchTableActivity.CourseActivity.isFinishing()){
                   startActivity(new Intent(SetCourseActivity.this,SearchTableActivity.class));
                }
                break;
            case R.id.bt_set_zc:
                showPopupWindow();
                break;
            case R.id.bt_set_xq:
                int year=Utility.getYear();
                final String[] arr=new String[]{(year - 1) + "-" + year + "-" + "1", (year - 1) + "-" + year + "-" + "2",
                        year + "-" + (year + 1) + "-" + "1", year + "-" + (year + 1) + "-" + "2"};
                new AlertDialog.Builder(SetCourseActivity.this)
                        .setTitle("请选择")
                        .setSingleChoiceItems(arr, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectXq=arr[which];
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Message message = new Message();
                                        message.obj = Ksoap2.getCourseInfo(mPreferences.getString("xh",""),selectXq);
                                        handler.sendMessage(message);
                                    }
                                }).start();

                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

                break;
            case R.id.bt_save:
                String zc=btSetZc.getText().toString();
                mEditor.putInt("currentZc", Integer.valueOf(zc));
                mEditor.putString("startDate", Utility.getDate());
                mEditor.putInt("startWeek",Utility.getWeekOfDate());
                mEditor.commit();
                SearchTableActivity.CourseActivity.finish();
                btSave.setVisibility(View.GONE);
                btSetZc.setText("当前周次  第"+zc+"周");
                Toast.makeText(SetCourseActivity.this,"已修改周次", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private void showPopupWindow() {
        View contentView = LayoutInflater.from(SetCourseActivity.this).inflate(R.layout.pw_layout_set, null);
        mPopupWindow = new PopupWindow(contentView);
        mPopupWindow.setWidth(AppBarLayout.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(AppBarLayout.LayoutParams.MATCH_PARENT);
        ArrayAdapter adapter=new ArrayAdapter(SetCourseActivity.this,R.layout.pw_item_layout,R.id.tv,getData());
        ListView lv=(ListView)contentView.findViewById(R.id.lv_zc);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.update();
        mPopupWindow.showAsDropDown(titleRl);
    }

    private List<String> getData(){
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 25; i++) {
                list.add( String.valueOf(i + 1));
        }
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPopupWindow.dismiss();
        btSetZc.setText(String.valueOf(position+1));
        btSave.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(SearchTableActivity.CourseActivity.isFinishing()){
            startActivity(new Intent(SetCourseActivity.this,SearchTableActivity.class));
        }
    }

    //设置沉浸式状态栏和导航栏
    private void initWindow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.colorPrimary));
            tintManager.setStatusBarTintEnabled(true);
        }
    }

}
