package com.wustwxy2.activity;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.wustwxy2.bean.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Login";
    Toolbar toolbar;
    EditText username;
    EditText password;
    Button login;
    private SystemBarTintManager tintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initToolbar();
        initWindow();
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
                SharedPreferences.Editor editor = sp.edit();
                String un = username.getText().toString();
                String pw = password.getText().toString();
                editor.putString("username",un);
                editor.putString("password",pw);
                editor.commit();
                BmobUser bu = new BmobUser();
                bu.setUsername(un);
                bu.setPassword(pw);
                bu.signUp(new SaveListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        if(e==null){
                            Log.i(TAG,"注册用户成功");
                        }
                        else
                            Log.e(TAG,e.getMessage());
                    }
                });
                bu.login(new SaveListener<BmobUser>()  {

                    @Override
                    public void done(BmobUser bmobUser, BmobException e) {
                        if(e==null){
                            Log.i(TAG, "用户登陆成功");
                            Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"登陆失败",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                Log.i(TAG,username.getText().toString() + "||" + password.getText().toString());
            }
        });
    }

    public void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("登录");
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

}
