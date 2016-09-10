package com.wustwxy2.activity;

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

import com.wustwxy2.R;

/**
 * Created by fubicheng on 2016/7/12.
 */
public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";
    Toolbar toolbar;
    //TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        initToolbar();
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
