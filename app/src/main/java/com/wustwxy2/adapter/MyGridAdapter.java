package com.wustwxy2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wustwxy2.R;
import com.wustwxy2.models.BaseViewHolder;

/**
 * Created by fubicheng on 2016/7/15.
 */
public class MyGridAdapter extends BaseAdapter {
    private Context mContext;

    public String[] img_text = { "图书馆", "课表", "成绩", "一卡通", "失物寻物", "校车",
            "地图", "计算机", "四六级", };
    public int[] imgs = {R.mipmap.lib, R.mipmap.table,
            R.mipmap.grade, R.mipmap.card,
            R.mipmap.losing, R.mipmap.bus,
            R.mipmap.map, R.mipmap.computer, R.mipmap.eng };

    public MyGridAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return img_text.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.grid_item, parent, false);
        }
        //使用BaseViewHolder来获取构造的View(Button)
        TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
        ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);
        iv.setBackgroundResource(imgs[position]);
        tv.setText(img_text[position]);
        return convertView;
    }

}
