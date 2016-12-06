package com.wustwxy2.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.TabLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.Text;
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
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by fubicheng on 2016/7/12.
 */
public class MesFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "MesFragment";
    Toolbar toolbar;
    TextView name;
    TextView no;
    TextView card_login;
    Button bind;
    Button feedback;
    Button update;
    Button about;
    TextView exit;
    //Button login;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    UpdateInfo info;
    String versionname;
    private static final int UPDATE_CLIENT = 0;
    private static final int GET_UNDATEINFO_ERROR = 1;
    private static final int DOWN_ERROR = 2;

    //TabLayout tabLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initToolbar();
        View view = inflater.inflate(R.layout.fragment_mes, container, false);
        name = (TextView)view.findViewById(R.id.mes_name);
        no = (TextView)view.findViewById(R.id.mes_no);
        card_login = (TextView) view.findViewById(R.id.mes_card_login);
        bind = (Button) view.findViewById(R.id.mes_bind);
        feedback = (Button) view.findViewById(R.id.mes_feedback);
        about = (Button) view.findViewById(R.id.mes_about);
        update = (Button) view.findViewById(R.id.mes_check_update);
        exit = (TextView) view.findViewById(R.id.exit);
        //login = (Button) view.findViewById(R.id.mes_login);
        initDataFromSP();
        BmobUser user = BmobUser.getCurrentUser(User.class);
        if(user==null){
            changeView();
        }else{
            //从缓存对象中得到名字和学号
            name.setText((String)BmobUser.getObjectByKey("nickname"));
            no.setText((String)BmobUser.getObjectByKey("username"));
        }
        //initToolbar();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bind.setOnClickListener(this);
        about.setOnClickListener(this);
        feedback.setOnClickListener(this);
        exit.setOnClickListener(this);
        //login.setOnClickListener(this);
        card_login.setOnClickListener(this);
        update.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //切换回来的时候判断是否已登录并复原
        changeViewBack();
    }

    public void initDataFromSP(){
        mPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public void initToolbar() {
        toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("个人信息");
        //fragment必须使用这句话才可以有Toolbar,Activity不需要
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu()");
        menu.clear();
        toolbar.setTitle("个人信息");
        inflater.inflate(R.menu.mainmenu, menu);
    }

    @Override
    public void onClick(View view) {
        if(view == bind){
            Intent intent = new Intent(getActivity() ,BindActivity.class);
            startActivity(intent);
        }else if(view == feedback){
            Intent intent = new Intent(getActivity() ,FeedbackActivity.class);
            startActivity(intent);
        }else if(view == about){
            Intent intent = new Intent(getActivity() ,AboutActivity.class);
            startActivity(intent);
        }else if(view == update){
            checkUpdate();
        }
        else if(view == exit){
            //退出
            BmobUser.logOut();
            BmobUser user = BmobUser.getCurrentUser(User.class);
            if(user==null ){
                Log.i(TAG, "USER NULL");
            }else {
                Log.i(TAG,"USER NOT NULL");
            }
            //清空SP里的数据
            emptySP();
            changeView();
        }else if(view == card_login){
            Intent intent = new Intent(getActivity(),LoginActivityInside.class);
            startActivity(intent);
        }
    }

    private void changeView(){
        name.setVisibility(View.GONE);
        no.setVisibility(View.GONE);
        about.setVisibility(View.GONE);
        bind.setVisibility(View.GONE);
        exit.setVisibility(View.GONE);
        feedback.setVisibility(View.GONE);
        update.setVisibility(View.GONE);
        //login.setVisibility(View.VISIBLE);
        card_login.setVisibility(View.VISIBLE);
    }


    //切换回来的时候判断是否已登录并复原
    private void changeViewBack(){
        BmobUser user = BmobUser.getCurrentUser(User.class);
        if(user!=null){
            //从缓存对象中得到名字和学号
            name.setVisibility(View.VISIBLE);
            no.setVisibility(View.VISIBLE);
            about.setVisibility(View.VISIBLE);
            bind.setVisibility(View.VISIBLE);
            exit.setVisibility(View.VISIBLE);
            feedback.setVisibility(View.VISIBLE);
            update.setVisibility(View.VISIBLE);
            //login.setVisibility(View.GONE);
            card_login.setVisibility(View.GONE);
            name.setText((String)BmobUser.getObjectByKey("nickname"));
            no.setText((String)BmobUser.getObjectByKey("username"));
        }
    }

    private void emptySP(){
        mPreferences = getActivity().getSharedPreferences("data",Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
        mEditor.putString("Account",null);
        mEditor.putString("password",null);
        mEditor.putString("nickname",null);
        mEditor.commit();
    }


    private void checkUpdate() {
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
    }

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
                    Toast.makeText(getActivity(), "当前已是最新版本！",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getActivity(), "获取服务器更新信息失败", Toast.LENGTH_SHORT).show();
                    //LoginMain();
                    break;
                case DOWN_ERROR:
                    //下载apk失败
                    Toast.makeText(getActivity(), "下载新版本失败", Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builer = new AlertDialog.Builder(getActivity()) ;
        builer.setTitle(R.string.find_new_version);
        builer.setMessage(info.getDescription());
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
        pd = new  ProgressDialog(getActivity());
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setProgressNumberFormat("%1d KB/%2d KB");
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

    public static File getFileFromServer(String path, ProgressDialog pd) throws Exception{
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            URL url = new URL(path);
            HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            pd.setMax(conn.getContentLength()/1024);
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
                pd.setProgress(total/1024);
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
* 用pull解析器解析服务器返回的xml文件 (xml封装了版本号)
*/
    public static UpdateInfo getUpdateInfo(InputStream is) throws Exception{
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "utf-8");//设置解析的数据源
        int type = parser.getEventType();
        UpdateInfo info = new UpdateInfo();//实体
        while(type != XmlPullParser.END_DOCUMENT ){
            switch (type) {
                case XmlPullParser.START_TAG:
                    if("version".equals(parser.getName())) {
                        info.setVersion(parser.nextText()); //获取版本号
                    }else if("notification".equals(parser.getName())){
                        info.setNotification(parser.nextText());//获取通知
                    }else if ("url".equals(parser.getName())){
                        info.setUrl(parser.nextText()); //获取要升级的APK文件
                    }else if ("description".equals(parser.getName())){
                        info.setDescription(parser.nextText()); //获取该文件的信息
                    }
                    break;
            }
            type = parser.next();
        }
        return info;
    }

    /*
    * 获取当前程序的版本号
   */
    private String getVersionName() throws Exception {
        //获取packagemanager的实例
        PackageManager packageManager = getActivity().getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
        return packInfo.versionName;
    }
}
