package com.wustwxy2.activity;

import android.os.Bundle;
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
import com.wustwxy2.models.AllNews;
import com.wustwxy2.models.College;
import com.wustwxy2.models.JWCActivity;
import com.wustwxy2.models.XFZXActivity;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        initToolbar();
        // InitView();
        //InitViewPager();
        viewPager = (ViewPager) view.findViewById(R.id.pager);
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
                    case 0://综合新闻
                        fragment = new AllNews();
                        break;
                    case 1://先锋在线
                        fragment = new XFZXActivity();
                        break;
                    case 2://教务处
                        fragment = new JWCActivity();
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
        //initTableLayout();

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
}
