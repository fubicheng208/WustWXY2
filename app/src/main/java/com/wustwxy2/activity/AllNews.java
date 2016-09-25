package com.wustwxy2.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wustwxy2.R;
import com.wustwxy2.adapter.NewsAdapter;
import com.wustwxy2.activity.BrowseNewsAvtivity;
import com.wustwxy2.models.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/16.
 */
public class AllNews extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private NewsAdapter adapter;
    private ListView lv;
    private List<News> newsList;
    private SwipeRefreshLayout refreshLayout;
    //无网络显示界面
    LinearLayout layout_no;
    TextView tv_no;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            String news = (String) msg.obj;
            //如果字符串长度小，即传递过来的是空字符串，则显示网站关闭信息
            if (news.length() < 10){
                newsList.add(new News("新闻网已关闭", null));
                if(refreshLayout!=null){
                    refreshLayout.setRefreshing(false);
                }
            }
            else {
                String getnews[] = news.split(",");
                for (int i = 0; i < 24; i += 2) {
                    newsList.add(new News(getnews[i], getnews[i + 1]));
                }
            }
            if(refreshLayout!=null)
                refreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_get_news, container, false);//关联布局文件
        lv = (ListView) rootView.findViewById(R.id.lvNews);
        newsList = new ArrayList<News>();
        adapter = new NewsAdapter(getActivity(), newsList);
        layout_no = (LinearLayout) rootView.findViewById(R.id.layout_no);
        tv_no = (TextView) rootView.findViewById(R.id.tv_no);
        //下拉刷新组件
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_news);
        //为组件加入参数
        initRefresh();
        refreshLayout.setOnRefreshListener(this);
        //如果没网，显示网路错误
        if(!isNetworkAvailable(getActivity())){
            showErrorView();
        }else{
            refreshLayout.post(new Runnable(){
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });
            onRefresh();
            parseHtml(handler);
        }
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {//监听事件实现界面跳转
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//listview点击响应事件
                News news = newsList.get(position);
                Intent intent = new Intent(getActivity(), BrowseNewsAvtivity.class);
                intent.putExtra("href", news.getHref());
                if (news.getHref() != null)
                    startActivity(intent);
            }
        });
        return rootView;
    }

    private void showErrorView() {
        lv.setVisibility(View.GONE);
        layout_no.setVisibility(View.VISIBLE);
        tv_no.setText("请检查您的网络");
        tv_no.setVisibility(View.VISIBLE);
    }

    private void showTrueView(){
        lv.setVisibility(View.VISIBLE);
        layout_no.setVisibility(View.GONE);
        tv_no.setVisibility(View.GONE);
    }

    private void initRefresh() {
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.yellow,
                R.color.titleLightBlue, R.color.green);
        refreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        refreshLayout.setProgressBackgroundColorSchemeResource(R.color.fresh_bg);
        refreshLayout.setProgressViewEndTarget(true, 175);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static void parseHtml(final Handler handler) {
        final String url = "http://www.cnwust.com/index.html";
        final String s = "http://www.cnwust.com";
        //使用线程，用Jsoup解析网页html文件，获取新闻标题和链接
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String str = "";

                        Document doc = Jsoup.connect(url).get();
                        Elements elements = doc.select("div .area_cen_inner").select("li");//根据位置找到新闻
                        for (Element ele : elements) {
                            str = str + ele.getElementsByTag("li").text() + "," + s + ele.getElementsByTag("a").first().attr("href") + ",";//获取标题和链接存入字符串
                            Elements elements1 = doc.select("div.area1").select("li");
                            for (Element ele1 : elements1)
                                str = str + ele1.getElementsByTag("li").text() + "," + s + ele1.getElementsByTag("a").first().attr("href") + ",";
                        }
                        Message msg = new Message();
                        msg.obj = str;
                        handler.sendMessage(msg);//用handle将字符串传给主线程

                } catch (Exception e) {
                    Message msg=new Message();
                    msg.obj=" ";
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onRefresh() {
        if(isNetworkAvailable(getActivity())){
            showTrueView();
            parseHtml(handler);
        }else{
            showErrorView();
            refreshLayout.setRefreshing(false);
        }
    }

    public boolean isNetworkAvailable(FragmentActivity activity)
    {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}