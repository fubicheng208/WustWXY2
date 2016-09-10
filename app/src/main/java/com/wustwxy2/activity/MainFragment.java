package com.wustwxy2.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wustwxy2.adapter.FindTabAdapter;
import com.wustwxy2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fubicheng on 2016/7/13.
 */
public class MainFragment extends Fragment {
    private TabLayout tab_FindFragment_title;           //定义TabLayout
    private ViewPager vp_FindFragment_pager;            //定义viewPager
    private FindTabAdapter fAdapter;                    //定义adapter

    private List<Fragment> list_fragment;                //定义要装fragment的列表
    private List<String> list_title;                     //tab名称列表

    private NewsFragment newsFragment;                   //新闻fragment
    private SearchFragment searchFragment;               //信息查询fragment
    private MesFragment mesFragment;                     //个人信息fragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initControls(view);

        return view;
    }

    /**
     * 初始化各控件
     */
    private void initControls(View view) {

        tab_FindFragment_title = (TabLayout)view.findViewById(R.id.sliding_tabs);
        vp_FindFragment_pager = (ViewPager)view.findViewById(R.id.viewpager);

        //初始化各fragment
        newsFragment = new NewsFragment();
        searchFragment = new SearchFragment();
        mesFragment = new MesFragment();

        //将fragment装进列表中
        list_fragment = new ArrayList<>();
        list_fragment.add(newsFragment);
        list_fragment.add(searchFragment);
        list_fragment.add(mesFragment);

        //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
        list_title = new ArrayList<>();
        list_title.add("新闻资讯");
        list_title.add("信息查询");
        list_title.add("个人信息");

        //设置TabLayout的模式
        tab_FindFragment_title.setTabMode(TabLayout.MODE_FIXED);
        //为TabLayout添加tab名称
        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(0)));
        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(1)));
        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(2)));


        fAdapter = new FindTabAdapter(getActivity().getSupportFragmentManager(),list_fragment,list_title);

        //viewpager加载adapter
        vp_FindFragment_pager.setAdapter(fAdapter);
        //tab_FindFragment_title.setViewPager(vp_FindFragment_pager);
        //TabLayout加载viewpager
        tab_FindFragment_title.setupWithViewPager(vp_FindFragment_pager);
        //tab_FindFragment_title.set
    }
}
