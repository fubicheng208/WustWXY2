package com.wustwxy2.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.wustwxy2.R;
import com.wustwxy2.util.DragImageView;
import com.wustwxy2.util.ImageLoadingDialog;

import java.io.File;

public class ImageShower extends Activity implements View.OnClickListener{

	String url;
	public static String IMAGE_CACHE_PATH = "imageloader/Cache"; // 图片缓存路径
	// 异步加载图片
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;

	private int window_width, window_height;// 控件宽度
	private DragImageView dragImageView;// 自定义控件
	private int state_height;// 状态栏的高度

	private ViewTreeObserver viewTreeObserver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageshower);
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();

		dragImageView = (DragImageView)findViewById(R.id.shower_photo);
		dragImageView.setOnClickListener(this);
		//获取传递过来的图片url
		url = getIntent().getStringExtra("url");

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

		final ImageLoadingDialog dialog = new ImageLoadingDialog(this);

		// progress dialog出现
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		//imageLoader加载图片
		mImageLoader.displayImage( url, dragImageView, options);
		dragImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		dragImageView.setmActivity(this);//注入Activity.
		/** 测量状态栏高度 **/
		viewTreeObserver = dragImageView.getViewTreeObserver();
		viewTreeObserver
				.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						if (state_height == 0) {
							// 获取状况栏高度
							Rect frame = new Rect();
							getWindow().getDecorView()
									.getWindowVisibleDisplayFrame(frame);
							state_height = frame.top;
							dragImageView.setScreen_H(window_height-state_height);
							dragImageView.setScreen_W(window_width);
						}

					}
				});

		//一分钟后progressbar消失
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
			}
		}, 1000 * 1);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		finish();
		return true;
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

    @Override
    public void onClick(View view) {
        if(view == dragImageView){
            finish();
        }
    }
}
