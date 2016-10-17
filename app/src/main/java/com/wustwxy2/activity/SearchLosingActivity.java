package com.wustwxy2.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class SearchLosingActivity extends BaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {

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

    //RelativeLayout progress;
    LinearLayout layout_no;
    TextView tv_no;

    //下拉刷新布局
    SwipeRefreshLayout refreshLayout;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_search_losing);
    }

    @Override
    public void initViews() {
        initToolbar();
        initWindow();
        //progress = (RelativeLayout) findViewById(R.id.progress);
        layout_no = (LinearLayout) findViewById(R.id.layout_no);
        tv_no = (TextView) findViewById(R.id.tv_no);

        layout_all = (LinearLayout) findViewById(R.id.layout_all);
        // Ĭ����ʧ�����
        tv_lost = (TextView) findViewById(R.id.tv_lost);
        tv_lost.setTag(getResources().getText(R.string.lost));
        listview = (ListView) findViewById(R.id.list_lost);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.losing_fresh);
        initRefresh();
    }

    @Override
    public void initListeners() {
        listview.setOnItemClickListener(this);
        listview.setOnScrollListener(this);
        layout_all.setOnClickListener(this);
        refreshLayout.setOnRefreshListener(this);
    }

    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.losing_toolbar);
        toolbar.setTitle("");
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    //���ó���ʽ״̬���͵�����
    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.colorPrimary));
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    private void initRefresh() {
        //设置圈圈颜色
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.yellow,
                R.color.titleLightBlue, R.color.green);
        //设置大小
        refreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        //设置背景颜色
        refreshLayout.setProgressBackgroundColorSchemeResource(R.color.fresh_bg);
        //设置距离顶部距离
        refreshLayout.setProgressViewEndTarget(true, 200);
    }

    @Override
    public void onClick(View v) {
        if (v == layout_all) {
            showListPop();
        } else if (v == layout_found) {
            morePop.dismiss();
            changeTextView(v);
            startRefresh();
            queryFounds();
        } else if (v == layout_lost) {
            changeTextView(v);
            morePop.dismiss();
            startRefresh();
            queryLosts();
        }
    }

    public void initData() {
        // TODO Auto-generated method stub
        if (LostAdapter == null) {
            LostAdapter = new QuickAdapter<Lost>(this, R.layout.item_list_losing) {
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
            FoundAdapter = new QuickAdapter<Found>(this, R.layout.item_list_losing) {
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
        startRefresh();
    }

    private void changeTextView(View v) {
        if (v == layout_found) {
            tv_lost.setTag(getResources().getText(R.string.found));
            tv_lost.setText(getResources().getText(R.string.found));
        } else {
            tv_lost.setTag(getResources().getText(R.string.lost));
            tv_lost.setText(getResources().getText(R.string.lost));
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
        if (from.equals(getResources().getText(R.string.lost))) {
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
        intent.putExtra("time", time);
        intent.putExtra("from", from);
        intent.putExtra("objectId", objectId);
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
                if (tag.equals(getResources().getText(R.string.lost))) {
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
                if (e == null) {
                    LostAdapter.clear();
                    FoundAdapter.clear();
                    if (losts == null || losts.size() == 0) {
                        showErrorView(0);
                        if (refreshLayout != null)
                            //刷新的spinner停止旋转
                            refreshLayout.setRefreshing(false);
                        LostAdapter.notifyDataSetChanged();
                        return;
                    }
                    //progress.setVisibility(View.GONE);
                    LostAdapter.addAll(losts);
                    listview.setAdapter(LostAdapter);
                    if (refreshLayout != null)
                        //刷新的spinner停止旋转
                        refreshLayout.setRefreshing(false);
                } else {
                    if (refreshLayout != null)
                        //刷新的spinner停止旋转
                        refreshLayout.setRefreshing(false);
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
                if (e == null) {
                    LostAdapter.clear();
                    FoundAdapter.clear();
                    if (founds == null || founds.size() == 0) {
                        showErrorView(1);
                        if (refreshLayout != null)
                            //刷新的spinner停止旋转
                            refreshLayout.setRefreshing(false);
                        FoundAdapter.notifyDataSetChanged();
                        return;
                    }
                    FoundAdapter.addAll(founds);
                    listview.setAdapter(FoundAdapter);
                    //progress.setVisibility(View.GONE);
                    if (refreshLayout != null) {
                        //刷新的spinner停止旋转
                        refreshLayout.setRefreshing(false);
                    }
                } else {
                    if (refreshLayout != null)
                        //刷新的spinner停止旋转
                        refreshLayout.setRefreshing(false);
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
        //progress.setVisibility(View.GONE);
        listview.setVisibility(View.GONE);
        layout_no.setVisibility(View.VISIBLE);
        if (tag == 0) {
            tv_no.setText(getResources().getText(R.string.list_no_data_lost));
        } else if (tag == 1) {
            tv_no.setText(getResources().getText(R.string.list_no_data_found));
        } else {
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
            case R.id.menu_losing_add://添加按钮
                BmobUser bmobUser = BmobUser.getCurrentUser(User.class);
                if (bmobUser != null) {
                    // �����û�ʹ��Ӧ��
                    Intent intent = new Intent(this, AddActivity.class);
                    intent.putExtra("from", tv_lost.getTag().toString());
                    startActivityForResult(intent, Constants.REQUESTCODE_ADD);
                } else {
                    ShowToast(getResources().getText(R.string.hint_login).toString());
                    Intent intent = new Intent(this, LoginActivityInside.class);
                    startActivity(intent);
                }
                break;
            case R.id.menu_losing_mine://我的按钮
                BmobUser bmobUser2 = BmobUser.getCurrentUser(User.class);
                if (bmobUser2 != null) {
                    // �����û�ʹ��Ӧ��
                    Intent intent1 = new Intent(this, MyLosingActivity.class);
                    intent1.putExtra("from", tv_lost.getTag().toString());
                    startActivityForResult(intent1, Constants.REQUESTCODE_MY);
                } else {
                    //�����û�����Ϊ��ʱ�� �ɴ��û�ע����桭
                    ShowToast(getResources().getText(R.string.hint_login).toString());
                    Intent intent = new Intent(this, LoginActivityInside.class);
                    startActivity(intent);
                }
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        if (tv_lost.getText().equals(getResources().getText(R.string.found))) {
            queryFounds();
        } else {
            queryLosts();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startRefresh();
    }

    //人工使刷新圈开始转动
    private void startRefresh(){
        refreshLayout.post(new Runnable(){
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        onRefresh();
    }

    //监听滑动事件，如到达第一项才可下滑更新
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(firstVisibleItem == 0){
            refreshLayout.setEnabled(true);
        }else{
            refreshLayout.setEnabled(false);
        }
    }

}
