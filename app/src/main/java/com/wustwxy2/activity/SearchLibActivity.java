package com.wustwxy2.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wustwxy2.R;

public class SearchLibActivity extends AppCompatActivity {

    Toolbar toolbar;
    private SystemBarTintManager tintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_lib);
        initToolbar();
        initWindow();
    }

    public void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Í¼Êé¹Ý");
        this.setSupportActionBar(toolbar);
    }

    //ÉèÖÃ³Á½þÊ½×´Ì¬À¸ºÍµ¼º½À¸
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
