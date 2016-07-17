package com.wustwxy2.utils;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.WindowManager;

import com.wustwxy2.R;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;                             //定义toolbar
    private MainFragment mf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragment(savedInstanceState);
        initToolbar();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }


}
