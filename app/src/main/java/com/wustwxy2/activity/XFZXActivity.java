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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wustwxy2.R;
import com.wustwxy2.adapter.BaseAdapterHelper;
import com.wustwxy2.adapter.NewsAdapter;
import com.wustwxy2.adapter.QuickAdapter;
import com.wustwxy2.bean.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import static com.wustwxy2.R.id.news_date;
import static com.wustwxy2.R.id.news_title;

/**
 * Created by Administrator on 2016/7/17.
 */
public class XFZXActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener ,AbsListView.OnScrollListener,
        AdapterView.OnItemClickListener{
    private static final String TAG = "XFZX";
    //private NewsAdapter adapter;
    protected QuickAdapter<News> newsAdapter;
    //private List<News> newsList;
    private ListView lv;
    private SwipeRefreshLayout refreshLayout;
    //无网络显示界面
    LinearLayout layout_no;
    TextView tv_no;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            String news= (String) msg.obj;
            Log.i(TAG,"NEWS:"+news);
            if(news.length()<10){//如果字符串长度小，即传递过来的是空字符串，则显示网站关闭信息
                newsAdapter.add(new News("先锋在线已关闭",null));
                if(refreshLayout!=null){
                    refreshLayout.setRefreshing(false);
                }
            }
            else {
                String getnews[] = news.split(",");
                for (int i = 0; i < 32; i += 2) {
                    String temp = getnews[i];
                    String text = temp.substring(0,temp.length()-10);
                    String date = temp.substring(temp.length()-10,temp.length());
                    //Log.i(TAG, "TEXT: " + text);
                    //Log.i(TAG,"DATE: " + date);
                    newsAdapter.add(new News(text,date, getnews[i + 1]));
                }
            }
            if(refreshLayout!=null)
                refreshLayout.setRefreshing(false);
            newsAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,  Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_get_news, container, false);//关联布局文件
        lv = (ListView) rootView.findViewById(R.id.lvNews);

        layout_no = (LinearLayout) rootView.findViewById(R.id.layout_no);
        tv_no = (TextView) rootView.findViewById(R.id.tv_no);
        //下拉刷新组件
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_news);
        //为组件加入参数
        //初始化列表
        initData();
        initRefresh();
        lv.setOnScrollListener(this);
        refreshLayout.setOnRefreshListener(this);
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

        return rootView;
    }

    private void initData(){
        if(newsAdapter == null){
            newsAdapter = new QuickAdapter<News>(getContext(), R.layout.news_item) {
                @Override
                protected void convert(BaseAdapterHelper helper, News item) {
                    helper.setText(news_title, item.getTitle())
                            .setText(news_date, item.getDateOrCollege());
                }
            };
        }
        lv.setAdapter(newsAdapter);
        lv.setOnItemClickListener(this);
    }


    private void initRefresh() {
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.yellow,
                R.color.titleLightBlue, R.color.green);
        refreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        refreshLayout.setProgressBackgroundColorSchemeResource(R.color.fresh_bg);
        refreshLayout.setProgressViewEndTarget(true, 175);

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

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    public static void parseHtml (final Handler handler){
        final String url="http://202.114.242.233:8036/default.html";

        new Thread(new Runnable() {
            @Override
            public void run() { //使用线程，用Jsoup解析网页html文件，获取新闻标题和链接
                try {
                    String str="";
                        Document doc = Jsoup.connect(url).get();
                        Elements elements = doc.select("div.mainframe_1_1_2").select("li");//获取HTML文件中指定位置的新闻
                        for (Element ele : elements) {
                            str = str+ele.getElementsByTag("li").text()+ ","+ele.getElementsByTag("a").first().attr("href")+",";
                        }
                        Elements elements1 = doc.select("div.mainframe_1_2").select("li");
                        for (Element ele1 : elements1) {
                            str = str+ele1.getElementsByTag("li").text()+ ","+ele1.getElementsByTag("a").first().attr("href")+",";
                        }

                        Message msg=new Message();//用message将字符串传给主线程
                        msg.obj=str;
                        handler.sendMessage(msg);
                }
                catch (Exception e) {//如果获取不到网页，则传递空字符串，由主线程进行处理
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
        newsAdapter.clear();
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

    //监听滑动事件，如到达第一项才可下滑更新
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(firstVisibleItem == 0){
            refreshLayout.setEnabled(true);
        }else{
            refreshLayout.setEnabled(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        News news = newsAdapter.getItem(i);
        Intent intent = new Intent(getActivity(), BrowseNewsAvtivity.class);
        intent.putExtra("href", news.getHref());
        if (news.getHref() != null)
            startActivity(intent);
    }
}
