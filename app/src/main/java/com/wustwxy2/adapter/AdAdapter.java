package com.wustwxy2.adapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wustwxy2.models.AdDomain;

import java.util.List;

/**
 * Created by fubicheng on 2016/7/14.
 */
public class AdAdapter extends PagerAdapter {

    private  List<AdDomain> adList;
    private List<ImageView> imageViews;
    public AdAdapter(List<AdDomain> List, List<ImageView> images){
        adList = List;
        imageViews = images;
    }

    @Override
    public int getCount() {
        return adList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView iv = imageViews.get(position);
        ((ViewPager) container).addView(iv);
        final AdDomain adDomain = adList.get(position);
        // 在这个方法里面设置图片的点击事件
        iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 处理跳转逻辑
            }
        });
        return iv;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {

    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {

    }

    @Override
    public void finishUpdate(View arg0) {

    }

}
