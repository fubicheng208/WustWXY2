package com.wustwxy2.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import com.wustwxy2.models.AdDomain;
import com.wustwxy2.models.MyGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by fubicheng on 2016/7/12.
 */
public class SearchFragment extends Fragment{

    private static final String TAG = "SearchFragment";
    Toolbar toolbar;
    //TabLayout tabLayout;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initToolbar();
        initGridView(view);
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
        return view;
    }

    public void initToolbar()
    {
        toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("信息查询");
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    /*public void initTableLayout() {
        tabLayout = (TabLayout)getActivity().findViewById(R.id.sliding_tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.titleLightBlue));
    }*/

    private void initGridView(View view) {
        gridview=(MyGridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(new MyGridAdapter(getActivity()));
        //控制点击后的跳转
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:{
                        startActivity(new Intent(getActivity(),SearchLibActivity.class));
                    }break;
                    case 1:{
                        startActivity(new Intent(getActivity(),SearchTableActivity.class));
                    }break;
                    case 2:{
                        startActivity(new Intent(getActivity(),SearchGradeActivity.class));
                    }break;
                    case 3:{
                        startActivity(new Intent(getActivity(),SearchCardActivity.class));
                    }break;
                    case 4:{
                        startActivity(new Intent(getActivity(),SearchLosingActivity.class));
                    }break;
                    case 5:{
                        startActivity(new Intent(getActivity(),SearchBusActivity.class));
                    }break;
                    case 6:{
                        startActivity(new Intent(getActivity(),SearchMapActivity.class));
                    }break;
                    case 7:{
                        startActivity(new Intent(getActivity(),SearchComputerActivity.class));
                    }break;
                    case 8:{
                        startActivity(new Intent(getActivity(),SearchEngActivity.class));
                    }break;
                }
            }
        });
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
        adViewPager.setAdapter(new AdAdapter(adList,imageViews));// 设置填充ViewPager页面的适配器
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
}
