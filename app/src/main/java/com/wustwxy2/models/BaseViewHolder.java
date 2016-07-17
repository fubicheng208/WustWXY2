package com.wustwxy2.models;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by fubicheng on 2016/7/15.
 */

//使用SparseArray来替代HashMap以提高性能，节省内存
//但SparseArray在数据离散和逆序查找的时候性能会低于HashMap
//具体见链接  http://www.open-open.com/lib/view/open1402906434918.html
public class BaseViewHolder {
    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }

}