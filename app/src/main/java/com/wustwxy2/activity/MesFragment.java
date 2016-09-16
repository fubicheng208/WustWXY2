package com.wustwxy2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.TabLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wustwxy2.R;
import com.wustwxy2.bean.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by fubicheng on 2016/7/12.
 */
public class MesFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "MesFragment";
    Toolbar toolbar;
    TextView name;
    TextView no;
    Button bind;
    Button feedback;
    Button about;
    Button exit;
    Button login;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    //TabLayout tabLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_mes, container, false);
        name = (TextView)view.findViewById(R.id.mes_name);
        no = (TextView)view.findViewById(R.id.mes_no);
        bind = (Button) view.findViewById(R.id.mes_bind);
        feedback = (Button) view.findViewById(R.id.mes_feedback);
        about = (Button) view.findViewById(R.id.mes_about);
        exit = (Button) view.findViewById(R.id.exit);
        login = (Button) view.findViewById(R.id.mes_login);
        initDataFromSP();
        BmobUser user = BmobUser.getCurrentUser(User.class);
        if(user==null){
            changeView();
        }
        //initToolbar();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bind.setOnClickListener(this);
        about.setOnClickListener(this);
        feedback.setOnClickListener(this);
        exit.setOnClickListener(this);
        login.setOnClickListener(this);

    }
    public void initDataFromSP(){
        mPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public void initToolbar() {
        toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("个人信息");
        //fragment必须使用这句话才可以有Toolbar,Activity不需要
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu()");
        menu.clear();
        toolbar.setTitle("个人信息");
        inflater.inflate(R.menu.mainmenu, menu);
    }

    @Override
    public void onClick(View view) {
        if(view == bind){
            Intent intent = new Intent(getActivity() ,BindActivity.class);
            startActivity(intent);
        }else if(view == feedback){
            Intent intent = new Intent(getActivity() ,FeedbackActivity.class);
            startActivity(intent);
        }else if(view == about){
            Intent intent = new Intent(getActivity() ,AboutActivity.class);
            startActivity(intent);
        }else if(view == exit){
            //退出
            BmobUser.logOut();
            BmobUser user = BmobUser.getCurrentUser(User.class);
            if(user==null ){
                Log.i(TAG, "USER NULL");
            }else {
                Log.i(TAG,"USER NOT NULL");
            }
            //清空SP里的数据
            emptySP();
            changeView();
        }else if(view ==login){
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
        }
    }


    private void changeView(){
        name.setVisibility(View.GONE);
        no.setVisibility(View.GONE);
        about.setVisibility(View.GONE);
        bind.setVisibility(View.GONE);
        exit.setVisibility(View.GONE);
        feedback.setVisibility(View.GONE);
        login.setVisibility(View.VISIBLE);

    }


    private void emptySP(){
        mPreferences = getActivity().getSharedPreferences("data",Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
        mEditor.putString("Account",null);
        mEditor.putString("password",null);
        mEditor.putString("nickname",null);
        mEditor.commit();
    }


}
