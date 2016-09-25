package com.wustwxy2.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.utils.L;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wustwxy2.R;
import com.wustwxy2.bean.User;
import com.wustwxy2.util.Ksoap2;
import com.wustwxy2.util.Utility;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_SIGNUP = 0;
    private static final String TAG = "Login";
    Toolbar toolbar;
    EditText username;
    EditText password;
    Button login;
    TextView tv;
    private SystemBarTintManager tintManager;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    //卡务中心SP保存的学号值，用来判断是否换号登录，若是则清空SP。
    String username_card;

    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.arg1) {
                //登录成功
                case 1:
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    break;
                //登录失败
                case 2:
                    Toast.makeText(LoginActivity.this, "登录失败，请检查您的网络", Toast.LENGTH_SHORT).show();
                    break;
                //账号或密码错误
                case 3:
                    Toast.makeText(LoginActivity.this, "账号名或密码错误", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(LoginActivity.this, "教务处又崩啦", Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //initToolbar();
        initWindow();
        username = (EditText)findViewById(R.id.input_account);
        password = (EditText)findViewById(R.id.input_password);
        tv = (TextView)findViewById(R.id.AsPass);
        login = (Button)findViewById(R.id.btn_login);

        login.setOnClickListener(this);
        tv.setOnClickListener(this);
        initSP();
        String cacheAccount = sp.getString("AccountCache",null);
        String cachePw = sp.getString("passwordCache",null);
        if(cacheAccount!=null&&cachePw!=null){
            username.setText(cacheAccount);
            password.setText(cachePw);
        }
        username_card = getSharedPreferences("WustCardCenter",0).getString("username","");
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

    @Override
    public void onClick(View view) {
        if(view == login){
            //有网络才登录
            if(isNetworkAvailable(this)){
                login();
            }else{
                Toast.makeText(LoginActivity.this,"请检查您的网路",Toast.LENGTH_SHORT).show();
            }
        }else if(view == tv){
            Intent intent = new Intent(this, MainActivity.class);
            //2 显示以游客方式
            intent.putExtra("from",2);
            startActivity(intent);
        }
    }

    public void login() {
        Log.d(TAG, "Login");
        BmobUser bmobUser = BmobUser.getCurrentUser();
        if(bmobUser != null){
            BmobUser.logOut();
            if(bmobUser == null){
                Log.i(TAG, "logOut成功");
            }
        }else{
            Log.i(TAG,"bmogUser已为空");
        }

        if (!validate()) {
            onLoginFailed();
            return;
        }

        login.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("登陆中");
        progressDialog.show();

        final String name = username.getText().toString();
        final String pw = password.getText().toString();

        // TODO: Implement your own authentication logic here.

        new Thread(new Runnable() {
            @Override
            public void run() {
                // On complete call either onLoginSuccess or onLoginFailed
                Log.i(TAG, "no:"+ name + "||pw:" + pw);
                String res = Ksoap2.getLoginInfo(name, pw);
                Log.i(TAG, "登录返回信息：" + res);
                if(res.equals("1")){
                    //获取名字
                    final String nickname = Utility.getName(Ksoap2.getScoreInfo(name));
                    User user = new User(name, pw);
                    user.setNickname(nickname);
                    user.signUp(new SaveListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            if(e==null){
                                Log.i(TAG,"注册用户成功");
                                //新登录用户，删除卡务中心保存的密码
                                if(!username_card.equals(name)){
                                   deleteCardSP();
                                }
                                write2Sp(name,pw,nickname);
                                Message msgMessage = new Message();
                                msgMessage.arg1 = 1;
                                handler.sendMessage(msgMessage);
                                progressDialog.dismiss();
                                //Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                //返回202表示账号名已存在
                                int errorCode = e.getErrorCode();
                                Log.e(TAG,"ErrorCode:" + e.getErrorCode());
                                if(user == null){
                                    Log.i(TAG, "user null");
                                }else{
                                    Log.i(TAG, "user not null");
                                }
                                if(errorCode == 202){
                                    User user2 = new User(name, pw);
                                    user2.setNickname(nickname);
                                    user2.login(new SaveListener<User>() {
                                        @Override
                                        public void done(User user, BmobException e) {
                                            if(e == null){
                                                Log.i(TAG,"登录成功");
                                                if(!username_card.equals(name)){
                                                    deleteCardSP();
                                                }
                                                write2Sp(name,pw,nickname);
                                                Message msgMessage = new Message();
                                                msgMessage.arg1 = 1;
                                                handler.sendMessage(msgMessage);
                                                progressDialog.dismiss();
                                                //Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }else{
                                                Log.i(TAG, "登录失败:" +e.getMessage() );
                                                Message msgMessage = new Message();
                                                msgMessage.arg1 = 2;
                                                handler.sendMessage(msgMessage);

                                                progressDialog.dismiss();
                                                //Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }else if(res.equals("100")){
                    Message msgMessage = new Message();
                    msgMessage.arg1 = 4;
                    handler.sendMessage(msgMessage);
                    //Toast.makeText(LoginActivity.this, "教务处又崩啦", Toast.LENGTH_SHORT).show();
                } else{
                    Message msgMessage = new Message();
                    msgMessage.arg1 = 3;
                    handler.sendMessage(msgMessage);

                    progressDialog.dismiss();
                    //Toast.makeText(LoginActivity.this, "账号名或密码错误", Toast.LENGTH_SHORT).show();
                }
                //onLoginSuccess();
                // onLoginFailed();
            }
        }).start();
        //progressDialog.dismiss();
        login.setEnabled(true);
    }

    private void initSP(){
        sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    private void write2Sp(String name, String password,String nickname){
        //用于在应用中检测是否登录
        editor.putString("Account",name);
        editor.putString("password",password);
        editor.putString("nickname",nickname);
        //用于保存记录，下次登录时免输入
        editor.putString("AccountCache", name);
        editor.putString("passwordCache",password);
        editor.commit();
    }

    private void deleteCardSP(){
        getSharedPreferences("WustCardCenter", 0).edit()
                .putString("username", null).apply();
        getSharedPreferences("WustCardCenter", 0).edit()
                .putString("password", null).apply();
    }

    public boolean isNetworkAvailable(AppCompatActivity activity)
    {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        login.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_LONG).show();
        login.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = username.getText().toString();
        String pw = password.getText().toString();

        if (name.isEmpty()||name.length()!=12 ) {
            username.setError("请输入有效的学号");
            valid = false;
        } else {
            username.setError(null);
        }

        if (pw.isEmpty()) {
            password.setError("密码为空");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }
}
