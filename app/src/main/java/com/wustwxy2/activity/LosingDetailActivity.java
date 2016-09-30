package com.wustwxy2.activity;



import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wustwxy2.R;
import com.wustwxy2.bean.Found;
import com.wustwxy2.bean.Lost;
import com.wustwxy2.bean.User;

import java.io.File;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;


public class LosingDetailActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "LosingDetail";


    // 异步加载图片
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    String url;

    public static String IMAGE_CACHE_PATH = "imageloader/Cache"; // 图片缓存路径

    private ProgressDialog dialog;

    Toolbar toolbar;
    private SystemBarTintManager tintManager;
    //正在获取数据时显示的界面
    RelativeLayout layout_progress;
    //获取到数据后显示的界面
    ScrollView layout_scrollView;
    //出错显示界面
    LinearLayout layout_no;
    TextView tv_no;

    TextView tv_nickname;
    TextView tv_time;
    TextView tv_phone;
    TextView tv_title;
    TextView tv_describe;
    //提供的缩略图
    ImageView photo_detail;
    ImageView back;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_losing_detail);
    }

    @Override
    public void initViews() {

        layout_progress = (RelativeLayout) findViewById(R.id.progress);
        layout_scrollView = (ScrollView)findViewById(R.id.scrollView_detail);
        layout_no = (LinearLayout) findViewById(R.id.layout_no);
        tv_no = (TextView) findViewById(R.id.tv_no);

        tv_nickname = (TextView)findViewById(R.id.tv_nickname);
        tv_time = (TextView)findViewById(R.id.tv_time_detail);
        tv_phone = (TextView)findViewById(R.id.tv_phone_detail);
        tv_title = (TextView)findViewById(R.id.tv_title_detail);
        tv_describe = (TextView)findViewById(R.id.tv_describe_detail);
        photo_detail = (ImageView)findViewById(R.id.losing_photo_detail);
        back = (ImageView) findViewById(R.id.iv_detail_back);
        //initToolbar();
        initWindow();
    }

    @Override
    public void initListeners() {
        tv_phone.setOnClickListener(this);
        photo_detail.setOnClickListener(this);
        back.setOnClickListener(this);
    }


    @Override
    public void initData() {
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

        //初始化对话框
        dialog = new ProgressDialog(this);
        //设置进度条样式
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        //失去焦点的时候，不是去对话框
        dialog.setCancelable(false);
        dialog.setMessage("正在加载...");
        showDetail();
    }

    private void showDetail(){
        showView();
        dialog.show();
        String objectId = getIntent().getStringExtra("objectId");
        final String time = getIntent().getStringExtra("time");
        final String phone = getIntent().getStringExtra("phone");
        final String title = getIntent().getStringExtra("title");
        final String describe = getIntent().getStringExtra("describe");
        String from = getIntent().getStringExtra("from");
        Log.i(TAG,"objectId:" + objectId);
        Log.i(TAG, "time:"+ time );
        Log.i(TAG, "phone:"+ phone );
        Log.i(TAG, "title:"+ title);
        Log.i(TAG, "describe:" + describe);
        if(from.equals(getResources().getText(R.string.lost))){
            BmobQuery<Lost> query = new BmobQuery<Lost>();
            query.include("author");
            query.getObject(objectId, new QueryListener<Lost>() {
                @Override
                public void done(Lost lost, BmobException e) {
                    if(e==null){
                        User user = lost.getAuthor();
                        String nickname = user.getNickname();
                        Log.i(TAG, "nickname:" + nickname);
                        if(nickname==null){
                            tv_nickname.setText(getResources().getText(R.string.default_name));
                        }else {
                            tv_nickname.setText(nickname);
                        }
                        tv_time.setText(time);
                        tv_phone.setText(phone);
                        tv_title.setText(title);
                        tv_describe.setText(describe);
                        url = lost.getPhotoUrl();
                        Log.i(TAG,"photoUrl:" + url);
                        if(url!=null){
                            // 异步加载图片
                            mImageLoader.displayImage( url, photo_detail, options);
                            photo_detail.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }
                        dialog.dismiss();
                    }else{
                        dialog.dismiss();
                        Log.d(TAG, e.getMessage());
                        ShowToast("显示详情失败，请检查您的网络");
                        showErrorView();
                    }
                }
            });
        }else{
            BmobQuery<Found> query = new BmobQuery<Found>();
            query.include("author");
            query.getObject(objectId, new QueryListener<Found>() {
                @Override
                public void done(Found found, BmobException e) {
                    if(e==null){
                        User user = found.getAuthor();
                        String userId = user.getObjectId();
                        Log.i(TAG, "userId" + userId);
                        String nickname = user.getNickname();
                        Log.i(TAG, "nickname:" + nickname);
                        if(nickname==null){
                            tv_nickname.setText(getResources().getText(R.string.default_name));
                        }else {
                            tv_nickname.setText(nickname);
                        }
                        tv_time.setText(time);
                        tv_phone.setText(phone);
                        tv_title.setText(title);
                        tv_describe.setText(describe);
                        String url = found.getPhotoUrl();
                        Log.i(TAG,"photoUrl:" + url);
                        if(url!=null){
                            // 异步加载图片
                            mImageLoader.displayImage( url, photo_detail, options);
                            photo_detail.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }
                        dialog.dismiss();
                    }else{
                        dialog.dismiss();
                        Log.d(TAG, e.getMessage());
                        ShowToast("显示详情失败，请检查您的网络");
                        showErrorView();
                    }
                }
            });
        }

    }

    private void showView() {
        layout_scrollView.setVisibility(View.VISIBLE);
        layout_progress.setVisibility(View.GONE);
    }

    private void showErrorView() {
        layout_scrollView.setVisibility(View.GONE);
        layout_progress.setVisibility(View.GONE);
        layout_no.setVisibility(View.VISIBLE);
        tv_no.setText(getResources().getText(R.string.losing_error));
    }


    //初始化图片加载器，调用的开源的图片加载框架
    private void initImageLoader() {
        File cacheDir = com.nostra13.universalimageloader.utils.StorageUtils
                .getOwnCacheDirectory(getApplicationContext(),
                        IMAGE_CACHE_PATH);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new LruMemoryCache(12 * 1024 * 1024))
                .memoryCacheSize(12 * 1024 * 1024)
                .discCacheSize(32 * 1024 * 1024).discCacheFileCount(100)
                .discCache(new UnlimitedDiscCache(cacheDir))
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();

        ImageLoader.getInstance().init(config);
    }

   /* public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.losing_toolbar_detail);
        toolbar.setTitle("");
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }*/


    //设置沉浸式状态栏和导航栏
    private void initWindow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.colorPrimary));
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == tv_phone){
            //根据号码拨打电话
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+tv_phone.getText().toString()));
            Log.i(TAG, "tel:" + tv_phone.getText().toString());
            startActivity(intent);
        }else if(view == photo_detail){
            Intent intent = new Intent(this,ImageShower.class);
            intent.putExtra("url", url);
            startActivity(intent);
        }else if(view == back){
            finish();
        }
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    /* public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }*/
}
