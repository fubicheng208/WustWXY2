package com.wustwxy2.models;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wustwxy2.R;
import com.wustwxy2.adapter.NewsAdapter;
import com.wustwxy2.activity.BrowseNewsAvtivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/16.
 */
public class AllNews extends Fragment {
    private NewsAdapter adapter;
    private List<News> newsList;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            String news = (String) msg.obj;
            if (news.length() < 10)//如果字符串长度小，即传递过来的是空字符串，则显示网站关闭信息
                newsList.add(new News("新闻网已关闭", null));
            else {
                String getnews[] = news.split(",");
                for (int i = 0; i < 24; i += 2) {
                    newsList.add(new News(getnews[i], getnews[i + 1]));

                }
            }
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
        ListView lv = (ListView) rootView.findViewById(R.id.lvNews);
        newsList = new ArrayList<News>();
        adapter = new NewsAdapter(getActivity(), newsList);
        parseHtml(handler);
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
}