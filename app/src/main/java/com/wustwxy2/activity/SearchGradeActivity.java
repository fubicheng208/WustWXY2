package com.wustwxy2.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wustwxy2.R;
import com.wustwxy2.adapter.ScoreAdapter;
import com.wustwxy2.models.JwInfoDB;
import com.wustwxy2.bean.Score;

import java.util.ArrayList;
import java.util.List;

public class SearchGradeActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout llClass;
    private LinearLayout llShow;
    private Button btBack;
    private TextView tvTitle;
    private TextView tvOtherInfo;
    private ListView lvScore;
    private Spinner spSelect;
    private LinearLayout llKcmc;
    private EditText etKcmc;
    private Button btQuery;
    private JwInfoDB mJwInfoDB;
    private SharedPreferences mPreferences;
    private ScoreAdapter mScoreAdapter;
    private List<Score> dataList;
    Toolbar toolbar;
    private SystemBarTintManager tintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_score);
        inite();
        spSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView)view;
                tv.setTextColor(getResources().getColor(R.color.colorText));
                if(position==2){
                    llKcmc.setVisibility(View.VISIBLE);
                }else {
                    llKcmc.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btQuery.setOnClickListener(this);
        //btBack.setOnClickListener(this);

    }

    private void inite(){
        llClass=(LinearLayout)findViewById(R.id.ll_class);
        llShow=(LinearLayout)findViewById(R.id.ll_show);
        //btBack=(Button)findViewById(R.id.bt_back);
        tvTitle=(TextView)findViewById(R.id.tv_title);
        tvOtherInfo=(TextView)findViewById(R.id.tv_other_info);
        lvScore=(ListView)findViewById(R.id.lv_score);
        spSelect=(Spinner)findViewById(R.id.sp_score);
        llKcmc=(LinearLayout)findViewById(R.id.ll_mc);
        etKcmc=(EditText)findViewById(R.id.et_kmmc);
        btQuery=(Button)findViewById(R.id.bt_query);
        mJwInfoDB=JwInfoDB.getJwInfoDB(this);
        mPreferences=getSharedPreferences("data",MODE_PRIVATE);
        dataList=new ArrayList<Score>();
        initToolbar();
        initWindow();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_query:
                String averageJd;
                if(dataList.size()>0){
                    dataList.retainAll(dataList);
                    mScoreAdapter.notifyDataSetChanged();
                }
                llClass.setVisibility(View.GONE);
                llShow.setVisibility(View.VISIBLE);
                toolbar.setTitle(spSelect.getSelectedItem().toString());
                //tvTitle.setText(spSelect.getSelectedItem().toString());
                switch (spSelect.getSelectedItemPosition()){
                    case 0:
                        dataList=mJwInfoDB.loadScores();
                        break;
                    case 1:
                        dataList=mJwInfoDB.loadOneTeamScores(mPreferences.getString("currentTeam",""));
                        break;
                    case 2:
                        String kcmc=etKcmc.getText().toString();
                        dataList=mJwInfoDB.loadOneScore(kcmc);
                        break;
                }
                tvOtherInfo.setText("姓名："+mPreferences.getString("xm","")+"\n学号："+
                        mPreferences.getString("xh","")+"\n平均绩点："+mPreferences.getString("averageJd",""));
                mScoreAdapter=new ScoreAdapter(SearchGradeActivity.this,R.layout.item,dataList);
                lvScore.setAdapter(mScoreAdapter);
                break;
            case R.id.bt_back:
                llClass.setVisibility(View.VISIBLE);
                llShow.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (llShow.getVisibility() == View.VISIBLE) {
            llClass.setVisibility(View.VISIBLE);
            llShow.setVisibility(View.GONE);
        } else {
            finish();
        }
    }

    public void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("成绩查询");
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




