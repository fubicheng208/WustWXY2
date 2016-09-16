package com.wustwxy2.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wustwxy2.R;
import com.wustwxy2.card.AccInfoActivity;
import com.wustwxy2.card.WustCardCenterLogin;

public class SearchCardActivity extends AppCompatActivity implements View.OnClickListener, WustCardCenterLogin.LoginListener{

    Toolbar toolbar;
    private SystemBarTintManager tintManager;
    private EditText username;
    private EditText password;
    private Button loginbButton;
    private CheckBox cBox;

    private WustCardCenterLogin login = new WustCardCenterLogin();
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_card);
        initToolbar();
        initWindow();
        username = (EditText) this.findViewById(R.id.card_user);
        password = (EditText) this.findViewById(R.id.card_password);
        loginbButton = (Button) this.findViewById(R.id.card_login_button);
        cBox=(CheckBox)findViewById(R.id.cb);


        loginbButton.setOnClickListener(this);
        login.setLoginListener(this);
        cBox.setChecked(true);
        username.setText(getSharedPreferences("WustCardCenter", 0).getString("username", ""));
        password.setText(getSharedPreferences("WustCardCenter", 0).getString("password", ""));
    }

    public void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("一卡通");
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnLoginCompleted(boolean bSuccess, String desc)
    {
        // TODO Auto-generated method stub
        progressDialog.dismiss();
        if(bSuccess)
        {
            getSharedPreferences("WustCardCenter", 0).edit().putString("username", username.getText().toString()).apply();
            getSharedPreferences("WustCardCenter", 0).edit().putString("password", password.getText().toString()).apply();
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();

            AccInfoActivity.login = login;
            Intent intent = new Intent(this, AccInfoActivity.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, desc, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        if(username.getText().toString().length() ==0)
        {
            Toast.makeText(this, "学号不可以为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.getText().toString().length() == 0)
        {
            Toast.makeText(this, "密码不可以为空", Toast.LENGTH_SHORT).show();
            return;
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive())
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在登录...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        login.login(username.getText().toString(), password.getText().toString());
    }
}

