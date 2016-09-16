package com.wustwxy2.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wustwxy2.R;
import com.wustwxy2.bean.User;

import cn.bmob.v3.BmobUser;

public class MainActivity extends BaseActivity {

    private Toolbar toolbar;     //定义toolbar
    private MainFragment mf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //如果得不到则为0,1为登录过，2为游客登录
        int from = getIntent().getIntExtra("from",0);
        BmobUser user = BmobUser.getCurrentUser(User.class);
        if(from!=2&&user==null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        setContentView();
        initFragment(savedInstanceState);
        initViews();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initViews() {
        initToolbar();
        initWindow();
    }

    //从父类继承的方法
    @Override
    public void initListeners() {

    }

    //从父类继承的方法
    @Override
    public void initData() {

    }

    public void initToolbar()
    {
        toolbar = (Toolbar)this.findViewById(R.id.toolbar);
        toolbar.setTitle("新闻资讯");                     // 标题的文字需在setSupportActionBar之前，不然会无效
        setSupportActionBar(toolbar);
    }

    /**
     * 为页面加载初始状态的fragment
     */
    public void initFragment(Bundle savedInstanceState)
    {
        //判断activity是否重建，如果不是，则不需要重新建立fragment.
        if(savedInstanceState==null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mf = new MainFragment();
            ft.replace(R.id.frame_main, mf).commit();
        }
    }

    //设置沉浸式状态栏但是通知栏仍为黑色
    private void initWindow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimary);
    }

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
}
