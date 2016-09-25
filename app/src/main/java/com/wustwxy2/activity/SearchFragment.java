package com.wustwxy2.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.wustwxy2.R;
import com.wustwxy2.adapter.AdAdapter;
import com.wustwxy2.adapter.MyGridAdapter;
import com.wustwxy2.bean.User;
import com.wustwxy2.util.WustCardCenterLogin;
import com.wustwxy2.models.AdDomain;
import com.wustwxy2.models.JwInfoDB;
import com.wustwxy2.models.MyGridView;
import com.wustwxy2.util.Ksoap2;
import com.wustwxy2.util.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by fubicheng on 2016/7/12.
 */
public class SearchFragment extends Fragment implements WustCardCenterLogin.LoginListener {

    private static final String TAG = "SearchFragment";
    Toolbar toolbar;
    //TabLayout tabLayout;
    private ProgressDialog progressDialog;

    //登录一卡通相关
    private WustCardCenterLogin login = new WustCardCenterLogin();
    //一卡通账号密码
    String username, password;

    //界面图片相关
    public static String IMAGE_CACHE_PATH = "imageloader/Cache"; // 图片缓存路径
    private ViewPager adViewPager;
    private List<ImageView> imageViews;// 滑动的图片集合
    private List<View> dots; // 图片标题正文的那些点
    private List<View> dotList;
    private int currentItem = 0; // 当前图片的索引号
    // 定义的五个指示点
    private View dot0;
    private View dot1;
    private View dot2;
    private View dot3;
    private View dot4;
    private ScheduledExecutorService scheduledExecutorService;
    // 异步加载图片
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    // 轮播banner的数据
    private List<AdDomain> adList;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            adViewPager.setCurrentItem(currentItem);
        }
    };
    //9个按钮界面
    private MyGridView gridview;

    //成绩课表相关
    private static final int QUERY_SCORE = 0;
    private static final int QUERY_COURSE = 1;
    private static final int DIALOG_SCORE = 2;
    private static final int DIALOG_COURSE = 3;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private JwInfoDB mJwInfoDB;
    private String xq;
    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!msg.obj.toString().equals("anyType{}")) {
                switch (msg.arg1) {
                    case QUERY_SCORE://查成绩
                        String cj = msg.obj.toString();
                        if (!cj.equals(mPreferences.getString("cjData", ""))) {
                            Utility.handleScores(mJwInfoDB, cj);
                            mEditor.putString("cjData", cj);
                            mEditor.putString("currentTeam", Utility.currentTeam);
                            mEditor.putString("xh", Utility.xh);
                            mEditor.putString("xm", Utility.xm);
                            mEditor.commit();
                        }
                        progressDialog.dismiss();
                        startActivity(new Intent(getActivity(), SearchGradeActivity.class));
                        break;
                    case QUERY_COURSE://查课表
                        String kb = msg.obj.toString();
                        if (!kb.equals(mPreferences.getString("kbData", ""))) {
                            Utility.handleCourses(mJwInfoDB, kb);
                            mEditor.putInt("currentZc", 1);
                            mEditor.putString("startDate", Utility.getDate());
                            mEditor.putInt("startWeek", Utility.getWeekOfDate());
                            mEditor.putString("xq", xq);
                            mEditor.putString("kbData", kb);
                            mEditor.commit();
                        }
                        progressDialog.dismiss();
                        startActivity(new Intent(getActivity(), SearchTableActivity.class));
                        break;
                    default:
                        break;
                }
            } else {
                Toast.makeText(getActivity(), R.string.no_data_toast, Toast.LENGTH_SHORT).show();
            }
        }
    };

    //由于多人代码的加入，不得不创建第三个handler，来响应UI更新
    private Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1){
                case DIALOG_SCORE:
                    progressDialog.setMessage("正在查询...");
                    progressDialog.show();
                    break;
                case DIALOG_COURSE:
                    progressDialog.setMessage("正在载入课表...");
                    progressDialog.show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initToolbar();
        //初始化网格布局
        initGridView(view);
        //初始化ProgressDialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在登录...");
        progressDialog.setCancelable(false);
        //初始化成绩课表所需变量
        initScoreAndCourse();
        //initTableLayout();
        // 使用ImageLoader之前初始化
        initImageLoader();
        // 获取图片加载实例
        mImageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.loading)
                .showImageForEmptyUri(R.mipmap.cry)
                .showImageOnFail(R.mipmap.cry)
                .cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY).build();
        initAdData(view);
        startAd();
        initGridView(view);
        login.setLoginListener(this);
        return view;
    }

    public void initToolbar() {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("信息查询");
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    private void initScoreAndCourse() {
        mPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
        mJwInfoDB = JwInfoDB.getJwInfoDB(getActivity());
    }
    /*public void initTableLayout() {
        tabLayout = (TabLayout)getActivity().findViewById(R.id.sliding_tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.titleLightBlue));
    }*/

    private void initGridView(View view) {
        gridview = (MyGridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(new MyGridAdapter(getActivity()));
        //控制点击后的跳转
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0: {
                        startActivity(new Intent(getActivity(), SearchLibActivity.class));
                    }
                    break;
                    case 1: {
                        final String xh1 = mPreferences.getString("Account", null);
                        if (xh1 != null) {
                            if (mPreferences.getString("kbData", "").isEmpty()) {
                                int year = Utility.getYear();
                                if (Utility.getDate().compareTo(year + "-" + "07-01") >= 0) {
                                    xq = year + "-" + (year + 1) + "-" + "1";
                                } else {
                                    xq = (year - 1) + "-" + year + "-" + "2";
                                }
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //通知主线程显示DIALOG
                                        Message message2 = new Message();
                                        message2.arg1 = DIALOG_COURSE;
                                        handler2.sendMessage(message2);
                                        Message message = new Message();
                                        //查询成绩
                                        message.arg1 = QUERY_COURSE;
                                        message.obj = Ksoap2.getCourseInfo("201513137125", xq);
                                        handler1.sendMessage(message);
                                    }
                                }).start();
                            } else {
                                startActivity(new Intent(getActivity(), SearchTableActivity.class));
                            }
                        } else {
                            Toast.makeText(getActivity(), "需要先登录的哦", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            getActivity().finish();
                        }
                    }
                    break;
                    case 2: {
                        if(isNetworkAvailable(getActivity())){
                            final String xh = mPreferences.getString("Account", null);
                            if (xh != null) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //通知主线程显示DIALOG
                                                Message message2 = new Message();
                                                message2.arg1 = DIALOG_SCORE;
                                                handler2.sendMessage(message2);
                                                //查询成绩
                                                Message message = new Message();
                                                message.arg1 = QUERY_SCORE;
                                                message.obj = Ksoap2.getScoreInfo(xh);
                                                handler1.sendMessage(message);
                                            }
                                        }).start();
                                    }
                                }).start();
                            } else {
                                Toast.makeText(getActivity(), "需要先登录的哦", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();

                            }
                        }else{
                            Toast.makeText(getActivity(), "请检查您的网络", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                    case 3: {
                        //一卡通
                        username = getActivity().getSharedPreferences("WustCardCenter", 0).getString("username", null);
                        password = getActivity().getSharedPreferences("WustCardCenter", 0).getString("password", null);
                        if (username == null || password == null) {
                            Toast.makeText(getActivity(), "请先绑定一卡通密码", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), BindActivity.class);
                            startActivity(intent);
                        } else {
                            progressDialog.show();
                            //调用WustCardCenterLogin类进行登录
                            login.login(username, password);
                            Log.i(TAG, username + "||" + password);
                        }
                    }
                    break;
                    case 4: {
                        startActivity(new Intent(getActivity(), SearchLosingActivity.class));
                    }
                    break;
                    case 5: {
                        startActivity(new Intent(getActivity(), SearchBusActivity.class));
                    }
                    break;
                    case 6: {
                        startActivity(new Intent(getActivity(), SearchMapActivity.class));
                    }
                    break;
                    case 7: {
                        startActivity(new Intent(getActivity(), SearchComputerActivity.class));
                    }
                    break;
                    case 8: {
                        startActivity(new Intent(getActivity(), SearchEngActivity.class));
                    }
                    break;
                }
            }
        });
    }

    //继承的的登录判断接口，如果登录成功则进入AccInfoActivity,否则跳转到
    //BindActivity重新绑定
    @Override
    public void OnLoginCompleted(boolean bSuccess, String desc) {
        progressDialog.dismiss();
        if (bSuccess) {
            Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_SHORT).show();
            AccInfoActivity.login = login;
            Intent intent = new Intent(getActivity(), AccInfoActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), desc, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), BindActivity.class);
            startActivity(intent);
        }
    }

    //初始化图片加载器，调用的开源的图片加载框架
    private void initImageLoader() {
        File cacheDir = com.nostra13.universalimageloader.utils.StorageUtils
                .getOwnCacheDirectory(getActivity().getApplicationContext(),
                        IMAGE_CACHE_PATH);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity()).defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new LruMemoryCache(12 * 1024 * 1024))
                .memoryCacheSize(12 * 1024 * 1024)
                .discCacheSize(32 * 1024 * 1024).discCacheFileCount(100)
                .discCache(new UnlimitedDiscCache(cacheDir))
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();

        ImageLoader.getInstance().init(config);
    }

    private void initAdData(View view) {
        // 广告数据
        adList = getBannerAd();

        imageViews = new ArrayList<ImageView>();

        // 点
        dots = new ArrayList<View>();
        dotList = new ArrayList<View>();
        dot0 = view.findViewById(R.id.v_dot0);
        dot1 = view.findViewById(R.id.v_dot1);
        dot2 = view.findViewById(R.id.v_dot2);
        dot3 = view.findViewById(R.id.v_dot3);
        dot4 = view.findViewById(R.id.v_dot4);
        dots.add(dot0);
        dots.add(dot1);
        dots.add(dot2);
        dots.add(dot3);
        dots.add(dot4);

        adViewPager = (ViewPager) view.findViewById(R.id.vp);
        adViewPager.setAdapter(new AdAdapter(adList, imageViews));// 设置填充ViewPager页面的适配器
        // 设置一个监听器，当ViewPager中的页面改变时调用
        adViewPager.setOnPageChangeListener(new MyPageChangeListener());
        addDynamicView();
    }

    private void addDynamicView() {
        // 动态添加图片和下面指示的圆点
        // 初始化图片资源
        for (int i = 0; i < adList.size(); i++) {
            ImageView imageView = new ImageView(getActivity());
            // 异步加载图片
            mImageLoader.displayImage(adList.get(i).getImgUrl(), imageView,
                    options);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageViews.add(imageView);
            dots.get(i).setVisibility(View.VISIBLE);
            dotList.add(dots.get(i));
        }
    }

    private void startAd() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 当Activity显示出来后，每五秒切换一次图片显示
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 5,
                TimeUnit.SECONDS);
    }

    /**
     * 轮播广播模拟数据
     *
     * @return
     */
    public static List<AdDomain> getBannerAd() {
        List<AdDomain> adList = new ArrayList<AdDomain>();

        AdDomain adDomain = new AdDomain();
        adDomain.setId("108078");
        adDomain.setImgUrl("http://bmob-cdn-5254.b0.upaiyun.com/2016/09/08/94d8ab279e3843afa5ea38da2480628a.jpg");
        adDomain.setAd(false);
        adList.add(adDomain);

        AdDomain adDomain2 = new AdDomain();
        adDomain2.setId("108078");
        adDomain2.setImgUrl("http://bmob-cdn-5254.b0.upaiyun.com/2016/09/08/dedb6516808041bdb6eb780305b446b3.jpg");
        adDomain2.setAd(false);
        adList.add(adDomain2);

        AdDomain adDomain3 = new AdDomain();
        adDomain3.setId("108078");
        adDomain3.setImgUrl("http://bmob-cdn-5254.b0.upaiyun.com/2016/09/08/4507575d5dc6413c805786bb02e1d0a1.jpg");
        adDomain3.setAd(false);
        adList.add(adDomain3);

        AdDomain adDomain4 = new AdDomain();
        adDomain4.setId("108078");
        adDomain4.setImgUrl("http://bmob-cdn-5254.b0.upaiyun.com/2016/09/08/14381f681b69433d997b66fe49fdb3f6.jpg");
        adDomain4.setAd(false);
        adList.add(adDomain4);

        AdDomain adDomain5 = new AdDomain();
        adDomain5.setId("108078");
        adDomain5.setImgUrl("http://bmob-cdn-5254.b0.upaiyun.com/2016/09/08/b24d9981cf134689a86b434153ff2663.jpg");
        adDomain5.setAd(true); // 代表是广告
        adList.add(adDomain5);

        return adList;
    }

    private class ScrollTask implements Runnable {

        @Override
        public void run() {
            synchronized (adViewPager) {
                currentItem = (currentItem + 1) % imageViews.size();
                handler.obtainMessage().sendToTarget();
            }
        }
    }

    //监听图片的切换
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        private int oldPosition = 0;

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            currentItem = position;
            AdDomain adDomain = adList.get(position);
            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
            dots.get(position).setBackgroundResource(R.drawable.dot_focused);
            oldPosition = position;
        }
    }

    //重新创建菜单时改变TableLayout和ToolBar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu()");
        menu.clear();
        toolbar.setTitle("信息查询");
        /*toolbar.setBackgroundColor(getResources().getColor(R.color.titleLightBlue));
        tabLayout.setBackgroundColor(getResources().getColor(R.color.titleLightBlue));*/
        inflater.inflate(R.menu.mainmenu, menu);
    }

    @Override
    public void onStop() {
        super.onStop();
        // 当Activity不可见的时候停止切换
        scheduledExecutorService.shutdown();
    }

    public boolean isNetworkAvailable(Activity activity)
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
