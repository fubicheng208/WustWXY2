package com.wustwxy2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wustwxy2.R;
import com.wustwxy2.bean.Score;

import java.util.List;

/**
 * Created by wsasus on 2016/7/17.
 */
public class ScoreAdapter extends ArrayAdapter<Score> {

    private int resourceId;

    public ScoreAdapter(Context context, int resource, List<Score> objects) {
        super(context, resource, objects);
        resourceId =resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Score score=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder=new ViewHolder();
            viewHolder.kcmc=(TextView)view.findViewById(R.id.tv_kcmc);
            viewHolder.zcj=(TextView)view.findViewById(R.id.tv_zcj);
            viewHolder.xf=(TextView)view.findViewById(R.id.tv_xf);
            viewHolder.jd=(TextView)view.findViewById(R.id.tv_jd);
            view.setTag(viewHolder);
        }else {
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.kcmc.setText(score.getKcmc());
        viewHolder.zcj.setText(score.getZcj());
        viewHolder.xf.setText(score.getXf());
        viewHolder.jd.setText(score.getJd());
        return view;
    }

    class ViewHolder{
        TextView kcmc;
        TextView zcj;
        TextView xf;
        TextView jd;
    }
}
