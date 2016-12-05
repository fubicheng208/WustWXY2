package com.wustwxy2.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wustwxy2.R;
import com.wustwxy2.adapter.FindTabAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fubicheng on 2016/7/12.
 */
public class NewsFragment extends Fragment {
    private static final String TAG = "NewsFragment";
    Toolbar toolbar;
    //TabLayout tabLayout;
    private ViewPager viewPager;
    private Button bt_one;
    private Button bt_two;
    private Button bt_three;
    private Button bt_four;

    private TabLayout tab_FindFragment_title;           //定义TabLayout
    private ViewPager vp_FindFragment_pager;            //定义viewPager
    private FindTabAdapter fAdapter;                    //定义adapter

    private List<Fragment> list_fragment;                //定义要装fragment的列表
    private List<String> list_title;                     //tab名称列表

    private JWCActivity JWC_fragment;
    private XFZXActivity XFZX_fragment;
    private AllNews ALL_fragment;
    private College COLLEGE_fragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        initToolbar();
        initControls(view);
        // InitView();
        //InitViewPager();
        /*viewPager = (ViewPager) view.findViewById(R.id.pager);
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                //实现界面转换
                switch (position) {
                    case 0://教务处
                        fragment = new JWCActivity();
                        break;
                    case 1://先锋在线
                        fragment = new XFZXActivity();
                        break;
                    case 2://综合新闻
                        fragment = new AllNews();
                        break;
                    case 3://学院新闻
                        fragment = new College();
                        break;


                }
                return fragment;
            }

        };
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                switch (position) {
                    //如果是点击的第一个button，那么就让第一个button的字体变为蓝色
                    //其他的button的字体的颜色变为黑色
                    case 0:
                        bt_one.setBackgroundColor(getResources().getColor(R.color.colorPrimaryNews));
                        bt_two.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkNews));
                        bt_three.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkNews));
                        bt_four.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkNews));
                        break;
                    case 1:
                        bt_one.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkNews));
                        bt_two.setBackgroundColor(getResources().getColor(R.color.colorPrimaryNews));
                        bt_three.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkNews));
                        bt_four.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkNews));
                        break;
                    case 2:
                        bt_one.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkNews));
                        bt_two.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkNews));
                        bt_three.setBackgroundColor(getResources().getColor(R.color.colorPrimaryNews));
                        bt_four.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkNews));
                        break;
                    case 3:
                        bt_one.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkNews));
                        bt_two.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkNews));
                        bt_three.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkNews));
                        bt_four.setBackgroundColor(getResources().getColor(R.color.colorPrimaryNews));
                        break;
                }
                super.onPageSelected(position);
            }
        });



        bt_one=(Button)view.findViewById(R.id.bt1);
        bt_two=(Button)view.findViewById(R.id.bt2);
        bt_three=(Button)view.findViewById(R.id.bt3);
        bt_four=(Button)view.findViewById(R.id.bt4);
        //设置点击监听
        bt_one.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        bt_two.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        bt_three.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                viewPager.setCurrentItem(2);
            }
        });
        bt_four.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                viewPager.setCurrentItem(3);
            }
        });
        bt_one.setBackgroundColor(getResources().getColor(R.color.colorPrimaryNews));
        bt_two.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkNews));
        bt_three.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkNews));
        bt_four.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkNews));
        //initTableLayout();*/

        return view;
    }

    public void initToolbar()
    {
        toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("新闻资讯");
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

   /* public void initTableLayout() {
        tabLayout = (TabLayout) getActivity().findViewById(R.id.sliding_tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.titleTeal));
    }*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu()");
        menu.clear();
        toolbar.setTitle("新闻资讯");
        /*toolbar.setBackgroundColor(getResources().getColor(R.color.titleTeal));
        tabLayout.setBackgroundColor(getResources().getColor(R.color.titleTeal));*/
        inflater.inflate(R.menu.mainmenu, menu);
    }

    /**
     * 初始化各控件
     */
    private void initControls(View view) {

        tab_FindFragment_title = (TabLayout)view.findViewById(R.id.news_sliding_tabs);
        vp_FindFragment_pager = (ViewPager)view.findViewById(R.id.news_pager);

        //初始化各fragment
        JWC_fragment = new JWCActivity();
        XFZX_fragment = new XFZXActivity();
        ALL_fragment = new AllNews();
        COLLEGE_fragment = new College();

        //将fragment装进列表中
        list_fragment = new ArrayList<>();
        list_fragment.add(JWC_fragment);
        list_fragment.add(XFZX_fragment);
        list_fragment.add(ALL_fragment);
        list_fragment.add(COLLEGE_fragment);

        //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
        list_title = new ArrayList<>();
        list_title.add("教务处");
        list_title.add("先锋在线");
        list_title.add("综合新闻");
        list_title.add("学院新闻");

        //设置TabLayout的模式
        tab_FindFragment_title.setTabMode(TabLayout.MODE_FIXED);
        //为TabLayout添加tab名称
        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(0)));
        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(1)));
        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(2)));
        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(3)));

        fAdapter = new FindTabAdapter(getActivity().getSupportFragmentManager(),list_fragment,list_title);

        //viewpager加载adapter
        vp_FindFragment_pager.setAdapter(fAdapter);
        vp_FindFragment_pager.setCurrentItem(0);
        //tab_FindFragment_title.setViewPager(vp_FindFragment_pager);
        //TabLayout加载viewpager
        tab_FindFragment_title.setupWithViewPager(vp_FindFragment_pager);
        //tab_FindFragment_title.set
    }
}
