package com.wustwxy2.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wustwxy2.R;
import com.wustwxy2.bean.Course;
import com.wustwxy2.models.JwInfoDB;
import com.wustwxy2.service.RemindService;
import com.wustwxy2.util.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.wustwxy2.R.id.tv;

public class SearchTableActivity extends BaseActivity implements AdapterView.OnItemClickListener ,AddCourseFragment.UpdateCourseListener{

    private RelativeLayout titleRl;
    private LinearLayout weekPanels[] = new LinearLayout[7];
    private int itemHeight;
    private int marTop, marLeft;
    private JwInfoDB mJwInfoDB;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private int currentZc;
    private TextView tvTitle;
    private ArrayAdapter mAdapter;
    private PopupWindow mPopupWindow;
    private PopupWindow mPopupWindowMenu;
    private RelativeLayout rlMenu;
    private Button btMenu;
    private ImageButton back;
    public static Activity CourseActivity;
    public static final int CROP_PHOTO=0;
    public static final int CHOOSE_PHOTO=1;
    private Uri imageUri;
    private File tempFile;
    Toolbar toolbar;
    private SystemBarTintManager tintManager;
    private static final String TAG = "SearchTableActivity";
    private static final String DIALOG_COURSE = "DIALOG_COURSE";
    private static final String DIALOG_ADD_COURSE = "DIALOG_ADD_COURSE";
    private static final String DIALOG_HELP_COURSE = "DIALOG_HELP_COURSE";
    private TextView textView;
    private int week=Utility.getWeekOfDate(),which=Utility.getCurrentClass();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        initToolbar();
        initWindow();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(1);
        back = (ImageButton) findViewById(R.id.course_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleRl= (RelativeLayout) findViewById(R.id.title_rl);
        itemHeight = getResources().getDimensionPixelSize(R.dimen.weekItemHeight);
        marTop = getResources().getDimensionPixelSize(R.dimen.weekItemMarTop);
        marLeft = getResources().getDimensionPixelSize(R.dimen.weekItemMarLeft);
        mJwInfoDB = JwInfoDB.getJwInfoDB(this);
        mPreferences = getSharedPreferences("data", MODE_PRIVATE);
        mEditor = mPreferences.edit();
        tvTitle = (TextView) findViewById(R.id.title);
        CourseActivity=this;
        tempFile=new File("/sdcard/Course/"+"background"+".jpg");// 文件名
        findViewById(R.id.bj_ll).setBackgroundDrawable(Drawable.createFromPath(tempFile.getAbsolutePath()));
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });

        updateZc();

        for (int i = 0; i < weekPanels.length; i++) {
            weekPanels[i] = (LinearLayout) findViewById(R.id.weekPanel_1 + i);
            initWeekPanel(weekPanels[i], mJwInfoDB.loadCourses(currentZc, i + 1));
        }

        if (textView != null) {
            ObjectAnimator a1 = ObjectAnimator.ofFloat(textView, "scaleY", 0.95f, 1.02f ,0.95f);
            ObjectAnimator a2 = ObjectAnimator.ofFloat(textView, "scaleX", 0.9f, 1.02f,0.9f);
            ObjectAnimator a3 = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0.5f, 1f);
            a1.setRepeatCount(Animation.INFINITE);
            a2.setRepeatCount(Animation.INFINITE);
            a3.setRepeatCount(Animation.INFINITE);
            AnimatorSet animSet = new AnimatorSet();
            animSet.play(a1).with(a2).with(a3);
            animSet.setDuration(2000);
            animSet.start();

//            AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.4f, 1.0f);
//            alphaAnimation1.setDuration(1000);
//            alphaAnimation1.setRepeatCount(Animation.INFINITE);
//            alphaAnimation1.setRepeatMode(Animation.REVERSE);
//            textView.setAnimation(alphaAnimation1);
//            alphaAnimation1.start();
        }else {
            Toast.makeText(this, "你今天没有课要上了，可以去happy啦~", Toast.LENGTH_SHORT).show();
        }

        Intent i=new Intent(this, RemindService.class);
        startService(i);
    }

    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.course_toolbar);
        this.setSupportActionBar(toolbar);
    }


    private void updateZc(){
        int days = Utility.getDays(mPreferences.getString("startDate", ""), Utility.getDate())
                + mPreferences.getInt("startWeek", 0);
        Log.d(TAG, "updateZc:"+"\nstartDate:"+mPreferences.getString("startDate", "")
        +"\nstartWeek:"+mPreferences.getInt("startWeek", 0)
        +"\ndays:"+days);
        currentZc = mPreferences.getInt("currentZc", 1);
        currentZc+=(days-1)/7;
        mEditor.putInt("currentZc", currentZc);
        mEditor.putInt("startWeek",Utility.getWeekOfDate());
        mEditor.putString("startDate",Utility.getDate());
        Log.d(TAG, "updateZc: "+"\ncurrentZc:"+currentZc+"\nstartWeek:"+Utility.getWeekOfDate());
        mEditor.commit();
        tvTitle.setText("第"+currentZc+"周");
    }

    @Override
    public void setContentView() {

    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {

    }

    public void updateCourse(int zc){
        for (int i = 0; i < weekPanels.length; i++) {
            weekPanels[i].removeAllViews();
            initWeekPanel(weekPanels[i], mJwInfoDB.loadCourses(zc, i + 1));
        }
    }

    private void initWeekPanel(LinearLayout ll, List<Course> data) {
        if (ll == null || data == null || data.size() < 1) return;
        Course pre = data.get(0);
        for (int i = 0; i < data.size(); i++) {
            Course c = data.get(i);
            TextView tv = new TextView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    itemHeight * c.getStep() + marTop * (c.getStep() - 1));
            if (i > 0) {
                lp.setMargins(marLeft, (c.getStart() - (pre.getStart() + pre.getStep())) * (itemHeight + marTop) + marTop, 0, 0);
            } else {
                lp.setMargins(marLeft, (c.getStart() - 1) * (itemHeight + marTop) + marTop, 0, 0);
            }
            if(c.getWeek()==week&&c.getStart()>=which&&textView==null){
                textView=tv;
            }
            tv.setLayoutParams(lp);
            tv.setGravity(Gravity.TOP);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setTextSize(12);
            tv.setTextColor(getResources().getColor(R.color.courseTextColor));
            tv.setText(c.getName() + "@" + c.getRoom());
            tv.setBackground(getResources()
                    .getDrawable(R.drawable.course_bg01 + (c.getClassCode()) % 20));
            setCourseOnClickListener(tv,c);
            ll.addView(tv);
            pre = c;
        }
    }

    private void setCourseOnClickListener(TextView textView, final Course course){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CourseInfoFragment dialog=CourseInfoFragment.newInstance(course);
                dialog.show(getSupportFragmentManager(),DIALOG_COURSE);
            }
        });
    }

    private List<String> getData() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 25; i++) {
            if(i!=currentZc-1) {
                list.add("第" + (i + 1) + "周");
            }else {
                list.add("第" + (i + 1) + "周(本周)");
            }
        }
        return list;
    }

    private void showPopupWindow() {
        View contentView = LayoutInflater.from(SearchTableActivity.this).inflate(R.layout.pw_layout, null);
        mPopupWindow = new PopupWindow(contentView);
        mPopupWindow.setWidth(AppBarLayout.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(AppBarLayout.LayoutParams.WRAP_CONTENT);
        mAdapter=new ArrayAdapter(SearchTableActivity.this,R.layout.pw_item_layout, tv,getData());
        ListView lv=(ListView)contentView.findViewById(R.id.lv_zc);
        TextView tv=(TextView)contentView.findViewById(R.id.tv);
        tv.setVisibility(View.GONE);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(this);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.update();
        mPopupWindow.showAtLocation(titleRl, Gravity.TOP| Gravity.CENTER, 0, 120);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.set_couse_menu, menu);
        return true;
    }




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPopupWindow.dismiss();
        if(position!=currentZc-1) {
            tvTitle.setText("第" + (position + 1) + "周(非本周)");

        }else {
            tvTitle.setText("第" + (position + 1) + "周");
        }
        updateCourse(position+1);
    }


    //设置背景图片相关
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CROP_PHOTO:
                if(resultCode==RESULT_OK){
                    findViewById(R.id.bj_ll).setBackgroundDrawable(Drawable.createFromPath(tempFile.getAbsolutePath()));
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    //Android 4.4及之后
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId= DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(
                    uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(  //////////////////////////
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(
                    uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath=getImagePath(uri,null);
        }
        displayImage(imagePath,"/sdcard/Course/",findViewById(R.id.bj_ll).getHeight(),
                findViewById(R.id.bj_ll).getWidth());
    }

    //Android 4.4之前
    private void handleImageBeforeKitKat(Intent data){
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        displayImage(imagePath,"/sdcard/Course/",findViewById(R.id.bj_ll).getHeight(),
                findViewById(R.id.bj_ll).getWidth());
    }

    //获取路径
    private String getImagePath(Uri uri, String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(
                uri,null,selection,null,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath, String savePath, int height, int width) {
        if (imagePath != null) {
            //DisplayMetrics dm = getResources().getDisplayMetrics();
            //Rect frame = new Rect();
            //getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            // 状态栏高度
            //int statusBarHeight = frame.top;
//            int height=findViewById(R.id.lay).getHeight();
            //getNavigationBarHeight(this)  //获取底部虚拟按钮的高度
            // dm.heightPixels   //获取除底部虚拟按钮的高度


            imageUri = Uri.fromFile(new File(imagePath));
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(imageUri, "image/*");
            intent.putExtra("scale", true);
            intent.putExtra("aspectX", width);// 裁剪框比例
            intent.putExtra("aspectY", height);
            intent.putExtra("outputX", width);// 输出图片大小
            intent.putExtra("outputY", height);
//            intent.putExtra("return-data", true);
            File temp = new File(savePath);//自已项目 文件夹
            if (!temp.exists()) {
                temp.mkdir();
            }
            intent.putExtra("outputFormat", "JPEG"); //输入文件格式
            intent.putExtra("output", Uri.fromFile(tempFile));  // 专入目标文件
            startActivityForResult(intent, CROP_PHOTO);
        } else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

   /* public void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("课表");
        this.setSupportActionBar(toolbar);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://增加点击事件
                finish();
                break;
            case R.id.menu_current_week:
                Intent intent=new Intent(SearchTableActivity.this,SetCourseActivity.class);
                intent.putExtra("XqOrZc",1);
                startActivity(intent);
                break;
            case R.id.menu_current_term:
                Intent intent1=new Intent(SearchTableActivity.this,SetCourseActivity.class);
                intent1.putExtra("XqOrZc",0);
                startActivity(intent1);
                break;
            case R.id.menu_set_bg:
                requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new BaseActivity.PermissionHandler() {
                    @Override
                    public void onGranted() {
                        Intent intent2=new Intent("android.intent.action.GET_CONTENT");
                        intent2.setType("image/*");
                        startActivityForResult(intent2,CHOOSE_PHOTO);
                    }

                    @Override
                    public void onDenied() {
                        Toast.makeText(SearchTableActivity.this, "由于您拒绝了权限申请，无法正常使用该功能", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public boolean onNeverAsk() {
                        new AlertDialog.Builder(SearchTableActivity.this)
                                .setTitle(R.string.permission_ask_title)
                                .setMessage(R.string.permission_mes)
                                .setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);

                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .setCancelable(false)
                                .show();
                        return  true;
                    }
                });
                break;
            case R.id.menu_add_course:
                AddCourseFragment dialog=AddCourseFragment.newInstance();
                dialog.show(getSupportFragmentManager(),DIALOG_ADD_COURSE);
                break;
            case R.id.menu_help:
                CourseHelpFragment dialog1=CourseHelpFragment.newInstance();
                dialog1.show(getSupportFragmentManager(),DIALOG_HELP_COURSE);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdateCourse() {
        tvTitle.setText("第" + (currentZc) + "周");
        updateCourse(currentZc);
    }
}
