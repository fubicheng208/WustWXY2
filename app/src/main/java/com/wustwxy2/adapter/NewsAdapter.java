package com.wustwxy2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wustwxy2.R;
import com.wustwxy2.bean.News;

import java.util.List;

/**
 * Created by Administrator on 2016/7/15.
 */
public class NewsAdapter extends BaseAdapter {
    private Context context;
    private TextView tvtitle;
    private List<News> newsList;
    public NewsAdapter(Context context, List<News> newsList){
        this.context=context;this.newsList=newsList;
    }




    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public News getItem(int position) {
        return newsList.get(position);
    }//获得位置信息

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    //界面匹配
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.news_item,null);
        }
        tvtitle=(TextView)convertView.findViewById(R.id.news_title);
        News news=newsList.get(position);
        tvtitle.setText(news.getTitle());
        return convertView;
    }
}
