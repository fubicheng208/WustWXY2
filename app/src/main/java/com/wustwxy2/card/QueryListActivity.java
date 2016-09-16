package com.wustwxy2.card;

/**
 * Created by ASUS on 2016/9/13.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.wustwxy2.R;

import java.util.List;
import java.util.Map;

public class QueryListActivity extends Activity
{
    public static String title;
    public static List<Map<String, String>> dataList;

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_querylist);

        listView = (ListView) this.findViewById(R.id.listview);

        setTitle(title);

        SimpleAdapter adapter = new SimpleAdapter(this, dataList, R.layout.listview_item
                , new String[]{"merchants", "datetime", "transtype", "volume", "balance"}
                , new int[]{R.id.merchants, R.id.datetime, R.id.transtype, R.id.volume, R.id.balance});
        listView.setAdapter(adapter);
    }
}
