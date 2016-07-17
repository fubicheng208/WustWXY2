package com.wustwxy2.utils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.wustwxy2.R;

public class SearchEngActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_eng);
        initToolbar();
    }

    public void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Ó¢ÓïËÄÁù¼¶");
        this.setSupportActionBar(toolbar);
    }
}
