package com.wustwxy2.activity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Toast;

import com.wustwxy2.config.Constants;

import cn.bmob.v3.Bmob;

/**
 * @author fubicheng
 * @ClassName: BaseActivity
 * @Description: TODO
 * @date 2016/7/29 17:57
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected int mScreenWidth;
    protected int mScreenHeight;

    public static final String TAG = "WXY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // ????? Bmob SDK
        Bmob.initialize(this, Constants.Bmob_APPID);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //????????????
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;

        setContentView();
        initViews();
        initListeners();
        initData();
    }
    /**
     * ???ò??????
     */
    public abstract void setContentView();

    /**
     * ?????????????е???
     */
    public abstract void initViews();

    /**
     * ?????????????
     */
    public abstract void initListeners();

    /** ????????????
     * initData
     */
    public abstract void initData();
    Toast mToast;

    public void ShowToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }

    /** ??????????????
     * getStateBar
     * @Title: getStateBar
     * @throws
     */
    public  int getStateBar(){
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        return statusBarHeight;
    }

    public static int dip2px(Context context, float dipValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int) (scale*dipValue+0.5f);
    }

}
