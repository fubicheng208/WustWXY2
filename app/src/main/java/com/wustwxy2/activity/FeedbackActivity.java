package com.wustwxy2.activity;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wustwxy2.R;
import com.wustwxy2.bean.Feedback;
import com.wustwxy2.bean.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    Toolbar toolbar;
    private SystemBarTintManager tintManager;
    EditText editText, email;
    Button button;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_feedback);
    }

    @Override
    public void initViews() {
        editText = (EditText)findViewById(R.id.feedback_et);
        email = (EditText) findViewById(R.id.feedback_email);
        button = (Button)findViewById(R.id.add_feedback);
        button.setOnClickListener(this);
        initToolbar();
        initWindow();
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("正在提交");

    }

    public void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("反馈");
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        if(view == button){
            if(validate())
                commitFeedback();
        }
    }

    private void commitFeedback() {
        dialog.show();
        String s_feedback = editText.getText().toString();
        String s_email = email.getText().toString();
        User user = BmobUser.getCurrentUser(User.class);
        Feedback feedback = new Feedback();
        feedback.setBack(s_feedback);
        if(!s_email.isEmpty())
            feedback.setEmail(email.getText().toString());
        if(user == null){
            Log.i(TAG, "USER NULL");
        }
        feedback.setAuthor(user);
        feedback.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    dialog.dismiss();
                    ShowToast("我们收到您的意见啦~");
                }else{
                    dialog.dismiss();
                    ShowToast("提交失败，请检查您的网络");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //检查输入内容是否合法
    private boolean validate(){
        boolean valid = true;
        Log.i(TAG, editText.getText().toString());
        if(editText.getText().toString().isEmpty()){
            ShowToast("反馈内容不可为空");
            valid = false;
        }
        String e = email.getText().toString();
        Log.i(TAG, e);
        //邮箱地址不为空才检查，即允许邮箱地址为空
        if(!e.isEmpty()){
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()){
                ShowToast("邮箱地址格式不正确");
                valid = false;
            }
        }
        return  valid;
    }

}
