package com.wustwxy2.activity;

/**
 * Created by ASUS on 2016/9/13.
 */

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wustwxy2.R;

import java.util.List;
import java.util.Map;

public class QueryListActivity extends AppCompatActivity
{

    Toolbar toolbar;
    private SystemBarTintManager tintManager;
    public static String title;
    public static List<Map<String, String>> dataList;

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_querylist);
        initToolbar();
        initWindow();

        listView = (ListView) this.findViewById(R.id.listview);

        setTitle(title);

        SimpleAdapter adapter = new SimpleAdapter(this, dataList, R.layout.listview_item_card
                , new String[]{"merchants", "datetime", "transtype", "volume", "balance"}
                , new int[]{R.id.merchants, R.id.datetime, R.id.transtype, R.id.volume, R.id.balance});
        listView.setAdapter(adapter);
    }

    public void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("消费明细");
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
}
