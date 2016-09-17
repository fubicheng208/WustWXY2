package com.wustwxy2.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wustwxy2.R;

import cn.bmob.v3.BmobUser;

public class BindActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BindActivity" ;
    Toolbar toolbar;
    private SystemBarTintManager tintManager;
    EditText et_card;
    Button bt_card;
    EditText et_lib;
    Button bt_lib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);
        et_card = (EditText) findViewById(R.id.et_bind_card);
        bt_card = (Button) findViewById(R.id.bt_bind_card);
        et_lib = (EditText) findViewById(R.id.et_bind_lib);
        bt_lib = (Button) findViewById(R.id.bt_bind_lib);
        bt_card.setOnClickListener(this);
        bt_lib.setOnClickListener(this);
        //图书馆功能还未实现故无法点击
        bt_lib.setEnabled(false);
        initToolbar();
        initWindow();
        String pw_card = getSharedPreferences("WustCardCenter", 0).getString("password",null);
        String pw_lib = getSharedPreferences("WustLib", 0).getString("password",null);
        if(pw_card!=null){
            et_card.setText(pw_card);
        }
        if(pw_lib!=null){
            et_lib.setText(pw_lib);
        }
    }

    public void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("绑定");
        this.setSupportActionBar(toolbar);
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

    @Override
    public void onClick(View view) {
        String pw_card = et_card.getText().toString();
        String pw_lib = et_lib.getText().toString();
        String username = (String) BmobUser.getObjectByKey("username");
        if(view == bt_card){
            if(pw_card.isEmpty()){
                et_card.setError("您还未输入密码");
            }else{
                et_card.setError(null);
                Log.i(TAG, "username: "+username);
                getSharedPreferences("WustCardCenter", 0).edit()
                        .putString("username", username).apply();
                getSharedPreferences("WustCardCenter", 0).edit()
                        .putString("password", pw_card).apply();
                Toast.makeText(this, "一卡通账号绑定成功", Toast.LENGTH_SHORT).show();
            }
        }else{
            if(pw_lib.isEmpty()){
                et_lib.setError("您还未输入密码");
            }else{
                et_lib.setError(null);
                Log.i(TAG, "username: "+username);
                getSharedPreferences("WustLib", 0).edit()
                        .putString("username", username).apply();
                getSharedPreferences("WustLib", 0).edit()
                        .putString("password", pw_lib).apply();
                Toast.makeText(this, "图书管账号绑定成功", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
