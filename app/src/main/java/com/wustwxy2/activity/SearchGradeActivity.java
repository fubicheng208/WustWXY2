package com.wustwxy2.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.wustwxy2.R;
import com.wustwxy2.adapter.ScoreAdapter;
import com.wustwxy2.models.JwInfoDB;
import com.wustwxy2.models.Score;

import java.util.ArrayList;
import java.util.List;

public class SearchGradeActivity extends Activity implements View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_score);
        inite();
        spSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
        btBack.setOnClickListener(this);


    }

    private void inite(){
        llClass=(LinearLayout)findViewById(R.id.ll_class);
        llShow=(LinearLayout)findViewById(R.id.ll_show);
        btBack=(Button)findViewById(R.id.bt_back);
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
                tvTitle.setText(spSelect.getSelectedItem().toString());
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
        if (llShow.getVisibility()== View.VISIBLE){
            llClass.setVisibility(View.VISIBLE);
            llShow.setVisibility(View.GONE);
        }else {
            finish();
        }
    }
}




