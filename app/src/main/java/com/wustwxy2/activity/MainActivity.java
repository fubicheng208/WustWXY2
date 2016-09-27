package com.wustwxy2.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wustwxy2.R;
import com.wustwxy2.bean.UpdateInfo;
import com.wustwxy2.bean.User;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.bmob.v3.BmobUser;

public class MainActivity extends BaseActivity {

    private Toolbar toolbar;     //定义toolbar
    private MainFragment mf;
    String versionname;
    UpdateInfo info;
    private static final int UPDATE_CLIENT = 0;
    private static final int GET_UNDATEINFO_ERROR = 1;
    private static final int DOWN_ERROR = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info = new UpdateInfo();
        CheckVersionTask task = new CheckVersionTask();
        Thread thread = new Thread(task);
        thread.start();
        try {
            versionname = getVersionName();
        } catch (Exception e) {
            Log.i(TAG,"得到versionname失败");
            e.printStackTrace();
        }

        //如果得不到则为0,1为登录过，2为游客登录
        int from = getIntent().getIntExtra("from",0);
        BmobUser user = BmobUser.getCurrentUser(User.class);
        if(from!=2&&user==null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        setContentView();
        initFragment(savedInstanceState);
        initViews();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initViews() {
        initToolbar();
        initWindow();
    }

    //从父类继承的方法
    @Override
    public void initListeners() {

    }

    //从父类继承的方法
    @Override
    public void initData() {

    }

    public void initToolbar()
    {
        toolbar = (Toolbar)this.findViewById(R.id.toolbar);
        toolbar.setTitle("新闻资讯");                     // 标题的文字需在setSupportActionBar之前，不然会无效
        setSupportActionBar(toolbar);
    }

    /**
     * 为页面加载初始状态的fragment
     */
    public void initFragment(Bundle savedInstanceState)
    {
        //判断activity是否重建，如果不是，则不需要重新建立fragment.
        if(savedInstanceState==null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mf = new MainFragment();
            ft.replace(R.id.frame_main, mf).commit();
        }
    }

    //设置沉浸式状态栏但是通知栏仍为黑色
    private void initWindow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimary);
    }

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    /*
     * 获取当前程序的版本号
    */
    private String getVersionName() throws Exception {
        //获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        return packInfo.versionName;
    }

    /*
 * 用pull解析器解析服务器返回的xml文件 (xml封装了版本号)
 */
    public static UpdateInfo getUpdateInfo(InputStream is) throws Exception{
        XmlPullParser  parser = Xml.newPullParser();
        parser.setInput(is, "utf-8");//设置解析的数据源
        int type = parser.getEventType();
        UpdateInfo info = new UpdateInfo();//实体
        while(type != XmlPullParser.END_DOCUMENT ){
            switch (type) {
                case XmlPullParser.START_TAG:
                    if("version".equals(parser.getName())){
                        info.setVersion(parser.nextText()); //获取版本号
                    }else if ("url".equals(parser.getName())){
                        info.setUrl(parser.nextText()); //获取要升级的APK文件
                    }
                    break;
            }
            type = parser.next();
        }
        return info;
    }

    public static File getFileFromServer(String path, ProgressDialog pd) throws Exception{
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            URL url = new URL(path);
            HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory(), "WxyUpdate.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len ;
            int total=0;
            while((len =bis.read(buffer))!=-1){
                fos.write(buffer, 0, len);
                total+= len;
                //获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        }
        else{
            return null;
        }
    }

    /*
 * 从服务器获取xml解析并进行比对版本号
 */
    public class CheckVersionTask implements Runnable{

        public void run() {
            try {
                //从资源文件获取服务器 地址
                String path = getResources().getString(R.string.serverurl);
                //包装成url的对象
                URL url = new URL(path);
                HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                InputStream is =conn.getInputStream();
                info =  getUpdateInfo(is);

                if(info.getVersion().equals(versionname)){
                    Log.i(TAG,"版本号相同无需升级");
                }else{
                    Log.i(TAG,"版本号不同 ,提示用户升级 ");
                    Message msg = new Message();
                    msg.what = UPDATE_CLIENT;
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                // 待处理
                Message msg = new Message();
                msg.what = GET_UNDATEINFO_ERROR;
                handler.sendMessage(msg);
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_CLIENT:
                    //对话框通知用户升级程序
                    showUpdataDialog();
                    break;
                case GET_UNDATEINFO_ERROR:
                    //服务器超时
                    Toast.makeText(getApplicationContext(), "获取服务器更新信息失败", Toast.LENGTH_SHORT).show();
                    //LoginMain();
                    break;
                case DOWN_ERROR:
                    //下载apk失败
                    Toast.makeText(getApplicationContext(), "下载新版本失败", Toast.LENGTH_SHORT).show();
                    //LoginMain();
                    break;
            }
        }
    };

    /*
     *
     * 弹出对话框通知用户更新程序
     *
     * 弹出对话框的步骤：
     *  1.创建alertDialog的builder.
     *  2.要给builder设置属性, 对话框的内容,样式,按钮
     *  3.通过builder 创建一个对话框
     *  4.对话框show()出来
     */
    protected void showUpdataDialog() {
        AlertDialog.Builder builer = new AlertDialog.Builder(this) ;
        builer.setTitle("版本升级");
        builer.setMessage("检测到最新版本,请及时更新!");
        //当点确定按钮时从服务器上下载 新的apk 然后安装
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG,"下载apk,更新");
                downLoadApk();
            }
        });
        //当点取消按钮时进行登录
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builer.create();
        dialog.show();
    }

    /*
     * 从服务器中下载APK
     */
    protected void downLoadApk() {
        final ProgressDialog pd;    //进度条对话框
        pd = new  ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.show();
        new Thread(){
            @Override
            public void run() {
                try {
                    File file = getFileFromServer(info.getUrl(), pd);
                    sleep(3000);
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = DOWN_ERROR;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }}.start();
    }

    //安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    /*
     * 进入程序的主界面
     */
   /* private void LoginMain(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        //结束掉当前的activity
        this.finish();
    }*/
}
