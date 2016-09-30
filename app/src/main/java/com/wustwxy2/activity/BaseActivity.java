package com.wustwxy2.activity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.wustwxy2.config.Constants;
import com.wustwxy2.util.PermissionUtils;

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

    InputMethodManager manager;

    /**
     * 权限回调Handler
     */
    private PermissionHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // ????? Bmob SDK
        Bmob.initialize(this, Constants.Bmob_APPID);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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

    //按在屏幕其他地方，软键盘消失
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 请求权限
     *
     * @param permissions 权限列表
     * @param handler     回调
     */
    protected void requestPermission(String[] permissions, PermissionHandler handler) {
        if (PermissionUtils.hasSelfPermissions(this, permissions)) {
            handler.onGranted();
        } else {
            mHandler = handler;
            ActivityCompat.requestPermissions(this, permissions, 001);
        }
    }


    /**
     * 权限请求结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (mHandler == null) return;

//        if (PermissionUtils.getTargetSdkVersion(this) < 23 && !PermissionUtils.hasSelfPermissions(this, permissions)) {
//            mHandler.onDenied();
//            return;
//        }

        if (PermissionUtils.verifyPermissions(grantResults)) {
            mHandler.onGranted();
        } else {
            if (!PermissionUtils.shouldShowRequestPermissionRationale(this, permissions)) {
                if (!mHandler.onNeverAsk()) {
                    Toast.makeText(this, "权限已被拒绝,请在设置-应用-权限中打开", Toast.LENGTH_SHORT).show();
                }

            } else {
                mHandler.onDenied();
            }
        }
    }


    /**
     * 权限回调接口
     */
    public abstract class PermissionHandler {
        /**
         * 权限通过
         */
        public abstract void onGranted();

        /**
         * 权限拒绝
         */
        public void onDenied() {
        }

        /**
         * 不再询问
         *
         * @return 如果要覆盖原有提示则返回true
         */
        public boolean onNeverAsk() {
            return false;
        }
    }

}
