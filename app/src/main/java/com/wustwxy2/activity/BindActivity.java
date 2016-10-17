package com.wustwxy2.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wustwxy2.R;
import com.wustwxy2.bean.User;
import com.wustwxy2.util.WustCardCenterLogin;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class BindActivity extends AppCompatActivity implements View.OnClickListener, WustCardCenterLogin.LoginListener {

    private static final String TAG = "BindActivity";
    Toolbar toolbar;
    private SystemBarTintManager tintManager;
    InputMethodManager manager;
    private WustCardCenterLogin login = new WustCardCenterLogin();
    ProgressDialog progressDialog;

    EditText et_card;
    Button bt_card;
    EditText et_lib;
    Button bt_lib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        et_card = (EditText) findViewById(R.id.et_bind_card);
        bt_card = (Button) findViewById(R.id.bt_bind_card);
        et_lib = (EditText) findViewById(R.id.et_bind_lib);
        bt_lib = (Button) findViewById(R.id.bt_bind_lib);
        bt_card.setOnClickListener(this);
        bt_lib.setOnClickListener(this);
        login.setLoginListener(this);
        //图书馆功能还未实现故无法点击
        bt_lib.setEnabled(false);
        initToolbar();
        initWindow();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在登录...");
        progressDialog.setCancelable(false);

        String pw_card = getSharedPreferences("WustCardCenter", 0).getString("password", null);
        String pw_lib = getSharedPreferences("WustLib", 0).getString("password", null);
        if (pw_card != null) {
            et_card.setText(pw_card);
        }
        if (pw_lib != null) {
            et_lib.setText(pw_lib);
        }
    }

    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("绑定");
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    //设置沉浸式状态栏和导航栏
    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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
        String username;
        BmobUser user = BmobUser.getCurrentUser(User.class);
        if (user == null) {
            Toast.makeText(this, "绑定之前要先登录哦", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivityInside.class));
        } else {
            username = (String) BmobUser.getObjectByKey("username");
            Log.i(TAG, "username: " + username);

            if (view == bt_card) {
                //绑定一卡通
                if (pw_card.isEmpty()) {
                    et_card.setError("您还未输入密码");
                } else {
                    progressDialog.show();
                    et_card.setError(null);
                    Log.i(TAG, "username: " + username);
                    getSharedPreferences("WustCardCenter", 0).edit()
                            .putString("username", username).apply();
                    getSharedPreferences("WustCardCenter", 0).edit()
                            .putString("password", pw_card).apply();
                    login.login(username, pw_card);
                    Log.i(TAG, username + "||" + pw_card);
                    //Toast.makeText(this, "一卡通账号绑定成功", Toast.LENGTH_SHORT).show();
                }
            } else {
                //绑定图书馆
                if (pw_lib.isEmpty()) {
                    et_lib.setError("您还未输入密码");
                } else {
                    et_lib.setError(null);
                    Log.i(TAG, "username: " + username);
                    getSharedPreferences("WustLib", 0).edit()
                            .putString("username", username).apply();
                    getSharedPreferences("WustLib", 0).edit()
                            .putString("password", pw_lib).apply();
                    Toast.makeText(this, "图书管账号绑定成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://增加点击事件
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //继承的的登录判断接口，如果登录成功则进入AccInfoActivity,否则显示登录失败
    @Override
    public void OnLoginCompleted(boolean bSuccess, String desc) {
        progressDialog.dismiss();
        if (bSuccess) {
            progressDialog.dismiss();
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            AccInfoActivity.login = login;
            Intent intent = new Intent(this, AccInfoActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, desc, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }
}
