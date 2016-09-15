package com.wustwxy2.card;

/**
 * Created by ASUS on 2016/9/13.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WustCardCenterLogin
{
    private static final String TAG = "WustCardCenterLogin";

    private LoginListener listener;
    private boolean bLogined = false;
    private String username;
    private String password;
    private Map<String, String> cookiesMap = null;
    private Map<String, Document> docMap = new HashMap<String, Document>();
    //Handler来根据接收的消息，处理UI更新。Thread线程发出Handler消息，通知更新UI。
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if(msg.what == 0)
            {
                bLogined = true;
                listener.OnLoginCompleted(true, (String) msg.obj);
            }
            else
            {
                listener.OnLoginCompleted(false, (String) msg.obj);
            }
        };
    };

    public interface LoginListener
    {
        public void OnLoginCompleted(boolean bSuccess, String desc);
    }

    public void setLoginListener(LoginListener listener)
    {
        this.listener = listener;
    }

    public void login(final String username, final String password)
    {
        this.username = username;
        this.password = password;
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                // TODO Auto-generated method stub
                try
                {

                    //Response对象用于动态响应客户端请示，控制发送给用户的信息，并将动态生成响应。Response对象只提供了一个数据集合cookie，它用于在客户端写入cookie值。若指定的cookie不存在，
                    //则创建它。若存在，则将自动进行更新。结果返回给客户端浏览器。
                    Response firstResponse = Jsoup.connect("http://card.wust.edu.cn/")
                            .method(Method.GET)
                            .execute();
                    cookiesMap = firstResponse.cookies();

                    Document doc = firstResponse.parse();
                    Map<String, String> datas = new HashMap<String, String>();
                    datas.put("__LASTFOCUS", doc.body().select("input[name=__LASTFOCUS]").val());
                    datas.put("__VIEWSTATE", doc.body().select("input[name=__VIEWSTATE]").val());
                    datas.put("UserLogin:txtUser", username);
                    datas.put("UserLogin:txtPwd", password);
                    datas.put("UserLogin:ddlPerson", "卡户");
                    String[] idsStrings = new String[]{ "UserLogin_ImgFirst", "UserLogin_imgSecond", "UserLogin_imgThird", "UserLogin_imgFour"};
                    String sureString = "";
                    for (String id : idsStrings)
                    {
                        String srcString = doc.select("#" + id).attr("src");
                        System.out.println("srcString: " + srcString);
                        sureString += srcString.charAt(7);
                    }
                    datas.put("UserLogin:txtSure", sureString);
                    datas.put("UserLogin:ImageButton1.x", "25");
                    datas.put("UserLogin:ImageButton1.y", "8");
                    datas.put("__EVENTTARGET", doc.body().select("input[name=__EVENTTARGET]").val());
                    datas.put("__EVENTARGUMENT", doc.body().select("input[name=__EVENTARGUMENT]").val());
                    datas.put("__EVENTVALIDATION", doc.body().select("input[name=__EVENTVALIDATION]").val());

                    Response response = Jsoup.connect("http://card.wust.edu.cn/default.aspx")
                            .data(datas)
                            .cookies(cookiesMap)
                            .header("Accept", "application/x-ms-application, image/jpeg, application/xaml+xml, image/gif, image/pjpeg, application/x-ms-xbap, */*")
                            .header("Accept-Encoding", "gzip, deflate")
                            .header("Accept-Language", "zh-CN")
                            .header("Cache-Control", "no-cache")
                            .header("Connection", "Keep-Alive")
                            .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                            .header("Host", "card.wust.edu.cn")
                            .header("Referer", "http://card.wust.edu.cn/default.aspx")
                            .header("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; Shuame)")
                            .method(Method.POST)
                            .execute();

                    doc = response.parse();

                    if(doc.toString().contains("密码非法"))
                    {
                        handler.obtainMessage(1, "输入的密码非法").sendToTarget();
                        return;
                    }

                    if (doc.toString().contains("登录失败"))
                    {
                        handler.obtainMessage(1, "用户名或密码错误").sendToTarget();
                        return;
                    }


                    docMap.put("Cardholder", getPagedDocument("http://card.wust.edu.cn/Cardholder/Cardholder.aspx"));
                    docMap.put("AccInfo", getPagedDocument("http://card.wust.edu.cn/Cardholder/AccInfo.aspx"));
                    docMap.put("AccBalance", getPagedDocument("http://card.wust.edu.cn/Cardholder/AccBalance.aspx"));
                    docMap.put("QueryCurrDetailFrame", getPagedDocument("http://card.wust.edu.cn/Cardholder/QueryCurrDetailFrame.aspx"));

                    handler.obtainMessage(0, "登录成功").sendToTarget();

                } catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    handler.obtainMessage(1, "网络异常").sendToTarget();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Document getPagedDocument(String url) throws IOException
    {
        return Jsoup.connect(url)
                .cookies(cookiesMap)
                .header("Accept", "*/*")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "zh-CN")
                .header("Cache-Control", "no-cache")
                .header("Connection", "Keep-Alive")
                .header("Host", "card.wust.edu.cn")
                .header("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; Shuame)")
                .get();
    }

    private String selectString(Document document, String cssSelector)
    {
        String val = "";
        if(bLogined && document != null)
        {
            try
            {
                val = document.select(cssSelector).text();
            } catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return val;
    }

    public String getID()
    {
        return selectString(docMap.get("AccInfo"), "#lblPerCode0");
    }

    public String getAccount()
    {
        //http://card.wust.edu.cn/Cardholder/ShowImage.aspx?AccNum=2011392501
        return selectString(docMap.get("AccInfo"), "#lblAcc0");
    }

    public Bitmap getPhoto()
    {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try
        {
            myFileUrl = new URL("http://card.wust.edu.cn/Cardholder/ShowImage.aspx?AccNum=" + getAccount());
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        try
        {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setRequestProperty("Cookie", "ASP.NET_SessionId=" + cookiesMap.get("ASP.NET_SessionId"));
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    public String getName()
    {
        return selectString(docMap.get("AccInfo"), "#lblName0");
    }

    public String getDept()
    {
        return selectString(docMap.get("AccInfo"), "#lblDep0");
    }

    public String getBalance()
    {
        return selectString(docMap.get("AccBalance"), "#lblOne0");
    }

    public String getTodayQueryTitle()
    {
        return selectString(docMap.get("QueryCurrDetailFrame"), "#lblText");
    }

    public List<Map<String, String>> getTodayQueryData()
    {
        List<Map<String, String>> list = new ArrayList<Map<String,String>>();
        Document document = docMap.get("QueryCurrDetailFrame");
        if(document != null)
        {
            Elements trElements = document.select("#dgShow > tbody:nth-child(1) > tr");
            String[] headersList = new String[]
                    {"sn", "acc", "cardtype", "transtype", "merchants",
                            "site", "terminal", "volume", "datetime", "wallet", "balance"};

            for (int i = 1; i < trElements.size(); i++)
            {
                Elements tdElements = trElements.get(i).select("td");
                Map<String, String> map = new HashMap<String, String>();
                for (int j = 0; j < tdElements.size(); j++)
                {
                    Log.i(TAG,headersList[j] + " : " + tdElements.get(j).text());
                    map.put(headersList[j], tdElements.get(j).text());
                }
                list.add(map);
            }
        }
        return list;
    }

    public List<Map<String, String>> getHistoryQueryData(String year, String month) throws Exception
    {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        //login(username, password);
        Response firstResponse = Jsoup
                .connect("http://card.wust.edu.cn/Cardholder/Queryhistory.aspx")
                .cookies(cookiesMap).method(Method.GET).execute();

        Document doc = firstResponse.parse();
        Map<String, String> datas = new HashMap<String, String>();
        datas.put("__VIEWSTATE", doc.body().select("input[name=__VIEWSTATE]")
                .val());
        datas.put("ddlYear", year);
        datas.put("ddlMonth", month);
        datas.put("txtMonth", month);
        datas.put("ImageButton1.x", "26");
        datas.put("ImageButton1.y", "9");

        Response response = Jsoup.connect("http://card.wust.edu.cn/Cardholder/Queryhistory.aspx")
                .data(datas)
                .cookies(cookiesMap)
                .header("Accept",
                        "application/x-ms-application, image/jpeg, application/xaml+xml, image/gif, image/pjpeg, application/x-ms-xbap, */*")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "zh-CN")
                .header("Cache-Control", "no-cache")
                .header("Connection", "Keep-Alive")
                .header("Content-Type",
                        "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Host", "card.wust.edu.cn")
                .header("Referer",
                        "http://card.wust.edu.cn/Cardholder/Queryhistory.aspx")
                .header("User-Agent",
                        "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; Shuame)")
                .method(Method.POST).execute();

        System.out.print(response.parse().toString());

        Document document = getPagedDocument("http://card.wust.edu.cn/Cardholder/QueryhistoryDetailFrame.aspx");

        System.out.print(document.toString());

        if (document != null)
        {
            Elements trElements = document
                    .select("#dgShow > tbody:nth-child(1) > tr");
            String[] headersList = new String[] { "sn", "acc", "cardtype",
                    "transtype", "merchants", "site", "terminal", "volume",
                    "datetime", "wallet", "balance" };

            for (int i = 1; i < trElements.size(); i++)
            {
                Elements tdElements = trElements.get(i).select("td");
                Map<String, String> map = new HashMap<String, String>();
                for (int j = 0; j < tdElements.size(); j++)
                {
                    Log.i(TAG, headersList[j] + " : "
                            + tdElements.get(j).text());
                    map.put(headersList[j], tdElements.get(j).text());
                }
                list.add(map);
            }
        }

        return list;
    }
}
