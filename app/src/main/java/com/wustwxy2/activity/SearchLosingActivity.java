package com.wustwxy2.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wustwxy2.R;
import com.wustwxy2.adapter.BaseAdapterHelper;
import com.wustwxy2.adapter.QuickAdapter;
import com.wustwxy2.bean.Found;
import com.wustwxy2.bean.Lost;
import com.wustwxy2.bean.User;
import com.wustwxy2.config.Constants;

import static com.wustwxy2.R.id.tv_describe;
import static com.wustwxy2.R.id.tv_photo;
import static com.wustwxy2.R.id.tv_time;
import static com.wustwxy2.R.id.tv_title;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SearchLosingActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private static final String TAG = " LOSING";

    Toolbar toolbar;
    private SystemBarTintManager tintManager;

    RelativeLayout layout_action;//
    LinearLayout layout_all;
    TextView tv_lost;
    ListView listview;

    protected QuickAdapter<Lost> LostAdapter;// ʧ��

    protected QuickAdapter<Found> FoundAdapter;// ����

    private Button layout_found;
    private Button layout_lost;
    PopupWindow morePop;

    RelativeLayout progress;
    LinearLayout layout_no;
    TextView tv_no;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_search_losing);
    }

    @Override
    public void initViews() {
        initToolbar();
        initWindow();
        progress = (RelativeLayout) findViewById(R.id.progress);
        layout_no = (LinearLayout) findViewById(R.id.layout_no);
        tv_no = (TextView) findViewById(R.id.tv_no);

        layout_all = (LinearLayout) findViewById(R.id.layout_all);
        // Ĭ����ʧ�����
        tv_lost = (TextView) findViewById(R.id.tv_lost);
        tv_lost.setTag("Lost");
        listview = (ListView) findViewById(R.id.list_lost);
    }

    @Override
    public void initListeners() {
        listview.setOnItemClickListener(this);
        layout_all.setOnClickListener(this);
    }

    public void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.losing_toolbar);
        toolbar.setTitle("");
        this.setSupportActionBar(toolbar);
    }


    //���ó���ʽ״̬���͵�����
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
    public void onClick(View v) {
        if (v == layout_all) {
            showListPop();
        } else if (v == layout_found) {
            morePop.dismiss();
            changeTextView(v);
            queryFounds();
        } else if (v == layout_lost) {
            changeTextView(v);
            morePop.dismiss();
            queryLosts();
        }
    }

    public void initData() {
        // TODO Auto-generated method stub
        if (LostAdapter == null) {
            LostAdapter = new QuickAdapter<Lost>(this, R.layout.item_list) {
                @Override
                protected void convert(BaseAdapterHelper helper, Lost lost) {
                    helper.setText(tv_title, lost.getTitle())
                            .setText(tv_describe, lost.getDescribe())
                            .setText(tv_time, lost.getCreatedAt())
                            .setText(tv_photo, lost.getPhone());
                }
            };
        }

        if (FoundAdapter == null) {
            FoundAdapter = new QuickAdapter<Found>(this, R.layout.item_list) {
                @Override
                protected void convert(BaseAdapterHelper helper, Found found) {
                    helper.setText(tv_title, found.getTitle())
                            .setText(tv_describe, found.getDescribe())
                            .setText(tv_time, found.getCreatedAt())
                            .setText(tv_photo, found.getPhone());
                }
            };
        }
        listview.setAdapter(LostAdapter);
        // Ĭ�ϼ���ʧ�����
        queryLosts();
    }

    private void changeTextView(View v) {
        if (v == layout_found) {
            tv_lost.setTag("Found");
            tv_lost.setText("Found");
        } else {
            tv_lost.setTag("Lost");
            tv_lost.setText("Lost");
        }
    }

    private void showListPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_lost, null);
        // ע��
        layout_found = (Button) view.findViewById(R.id.layout_found);
        layout_lost = (Button) view.findViewById(R.id.layout_lost);
        layout_found.setOnClickListener(this);
        layout_lost.setOnClickListener(this);
        morePop = new PopupWindow(view, mScreenWidth, 600);

        morePop.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    morePop.dismiss();
                    return true;
                }
                return false;
            }
        });

        morePop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        morePop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        morePop.setTouchable(true);
        morePop.setFocusable(true);
        morePop.setOutsideTouchable(true);
        morePop.setBackgroundDrawable(new BitmapDrawable());
        // ����Ч�� �Ӷ�������
        morePop.setAnimationStyle(R.style.MenuPop);
        morePop.showAsDropDown(layout_all, 0, -dip2px(this, 2.0F));
    }

    int position;
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        position = i;
        Intent intent = new Intent(this, LosingDetailActivity.class);
        String title = "";
        String describe = "";
        String phone = "";
        String time = "";
        String objectId = "";
        String from = tv_lost.getText().toString();
        if (from.equals("Lost")) {
            title = LostAdapter.getItem(position).getTitle();
            describe = LostAdapter.getItem(position).getDescribe();
            phone = LostAdapter.getItem(position).getPhone();
            time = LostAdapter.getItem(position).getCreatedAt();
            objectId = LostAdapter.getItem(position).getObjectId();

        } else {
            title = FoundAdapter.getItem(position).getTitle();
            describe = FoundAdapter.getItem(position).getDescribe();
            phone = FoundAdapter.getItem(position).getPhone();
            time = FoundAdapter.getItem(position).getCreatedAt();
            objectId = FoundAdapter.getItem(position).getObjectId();
        }
        intent.putExtra("describe", describe);
        intent.putExtra("phone", phone);
        intent.putExtra("title", title);
        intent.putExtra("time",time);
        intent.putExtra("from", from);
        intent.putExtra("objectId",objectId);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.REQUESTCODE_ADD:// ��ӳɹ�֮��Ļص�
                String tag = tv_lost.getTag().toString();
                if (tag.equals("Lost")) {
                    queryLosts();
                } else {
                    queryFounds();
                }
                break;
        }
    }

    /**
     * ��ѯȫ��ʧ����Ϣ queryLosts
     *
     * @return void
     * @throws
     */
    private void queryLosts() {
        showView();
        BmobQuery<Lost> query = new BmobQuery<Lost>();
        query.order("-createdAt");// ����ʱ�併��
        query.findObjects(new FindListener<Lost>() {
            @Override
            public void done(List<Lost> losts, BmobException e) {
                if(e==null){
                    LostAdapter.clear();
                    FoundAdapter.clear();
                    if (losts == null || losts.size() == 0) {
                        showErrorView(0);
                        LostAdapter.notifyDataSetChanged();
                        return;
                    }
                    progress.setVisibility(View.GONE);
                    LostAdapter.addAll(losts);
                    listview.setAdapter(LostAdapter);
                }
                else{
                    showErrorView(2);
                }
            }
        });
    }

    public void queryFounds() {
        showView();
        BmobQuery<Found> query = new BmobQuery<Found>();
        query.order("-createdAt");// ����ʱ�併��
        query.findObjects(new FindListener<Found>() {

            @Override
            public void done(List<Found> founds, BmobException e) {
                if(e==null){
                    LostAdapter.clear();
                    FoundAdapter.clear();
                    if (founds == null || founds.size() == 0) {
                        showErrorView(1);
                        FoundAdapter.notifyDataSetChanged();
                        return;
                    }
                    FoundAdapter.addAll(founds);
                    listview.setAdapter(FoundAdapter);
                    progress.setVisibility(View.GONE);
                }
                else{
                    showErrorView(2);
                }
            }
        });
    }

    /**
     * ����������������ʱ����ʾ�Ľ��� showErrorView
     *
     * @return void
     * @throws
     */
    private void showErrorView(int tag) {
        progress.setVisibility(View.GONE);
        listview.setVisibility(View.GONE);
        layout_no.setVisibility(View.VISIBLE);
        if (tag == 0) {
            tv_no.setText(getResources().getText(R.string.list_no_data_lost));
        } else if(tag == 1){
            tv_no.setText(getResources().getText(R.string.list_no_data_found));
        }else {
            tv_no.setText(getResources().getText(R.string.losing_error));
        }
    }

    private void showView() {
        listview.setVisibility(View.VISIBLE);
        layout_no.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.losingmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.menu_losing_add:
                BmobUser bmobUser = BmobUser.getCurrentUser(User.class);
                if(bmobUser != null){
                    // �����û�ʹ��Ӧ��
                    Intent intent = new Intent(this, AddActivity.class);
                    intent.putExtra("from", tv_lost.getTag().toString());
                    startActivityForResult(intent, Constants.REQUESTCODE_ADD);
                }else{
                    ShowToast(getResources().getText(R.string.hint_login).toString());
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.menu_losing_mine:
                BmobUser bmobUser2 = BmobUser.getCurrentUser(User.class);
                if(bmobUser2 != null){
                    // �����û�ʹ��Ӧ��
                    Intent intent1 = new Intent(this, MyLosingActivity.class);
                    intent1.putExtra("from", tv_lost.getTag().toString());
                    startActivityForResult(intent1, Constants.REQUESTCODE_MY);
                }else{
                    //�����û�����Ϊ��ʱ�� �ɴ��û�ע����桭
                    ShowToast(getResources().getText(R.string.hint_login).toString());
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.menu_losing_login:
                Intent intent2 = new Intent(this, LoginActivity.class);
                startActivity(intent2);
        }
        return super.onOptionsItemSelected(item);
    }
}
