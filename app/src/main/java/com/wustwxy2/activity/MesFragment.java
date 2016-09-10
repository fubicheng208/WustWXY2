package com.wustwxy2.activity;

import android.content.Context;
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
import android.widget.Toast;

import com.wustwxy2.R;
import com.wustwxy2.bean.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by fubicheng on 2016/7/12.
 */
public class MesFragment extends Fragment {

    private static final String TAG = "MesFragment";
    Toolbar toolbar;

    //TabLayout tabLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mes, container, false);
        initToolbar();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

}
