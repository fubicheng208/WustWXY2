package com.wustwxy2.card;

/**
 * Created by ASUS on 2016/9/13.
 */


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wustwxy2.R;


public class AccInfoActivity extends Activity
{
    public static WustCardCenterLogin login;
    private  Bitmap  bitmap3=null;

    private ImageView imageView;
    private TextView username;
    private TextView userid;
    private TextView userdept;
    private TextView userbalance;

    private Button btnquerytoday;
    private Button btnqueryhistory;
    private Button btnlossreport;

    private final int MSG_IMAGELOAD = 0;
    private final int MSG_QUERYCOMPLETE = 1;
    private final int MSG_QUERYERROR = 2;


    private ProgressDialog progressDialog;

    private Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case MSG_IMAGELOAD:
                    imageView.setImageBitmap((Bitmap) msg.obj);
                    break;

                case MSG_QUERYCOMPLETE:
                {
                    progressDialog.dismiss();
                    Intent intent = new Intent(AccInfoActivity.this, QueryListActivity.class);
                    startActivity(intent);
                    break;
                }

                case MSG_QUERYERROR:
                {
                    progressDialog.dismiss();
                    Toast.makeText(AccInfoActivity.this, "查询失败,请重试!", Toast.LENGTH_LONG).show();
                    break;
                }
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_accinfo);
        imageView = (ImageView) this.findViewById(R.id.imageview);

        username = (TextView) this.findViewById(R.id.username);
        userid = (TextView) this.findViewById(R.id.userid);
        userdept = (TextView) this.findViewById(R.id.userdept);
        userbalance = (TextView) this.findViewById(R.id.userbalance);

        btnquerytoday = (Button) this.findViewById(R.id.btnquerytoday);
        btnqueryhistory = (Button) this.findViewById(R.id.btnqueryhistory);
        btnlossreport = (Button) this.findViewById(R.id.btnlossreport);

        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                // TODO Auto-generated method stub
                Bitmap photo = login.getPhoto();
                if(photo == null) System.out.println("photo null");
                handler.obtainMessage(MSG_IMAGELOAD, photo).sendToTarget();
            }
        }).start();

        username.setText(login.getName());
        userid.setText(login.getID());
        userdept.setText(login.getDept());
        userbalance.setText("余  额: " + login.getBalance());

        btnquerytoday.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                progressDialog = new ProgressDialog(AccInfoActivity.this);
                progressDialog.setMessage("正在查询...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        QueryListActivity.title = login.getTodayQueryTitle();
                        QueryListActivity.dataList = login.getTodayQueryData();
                        handler.obtainMessage(MSG_QUERYCOMPLETE).sendToTarget();
                    }
                }).start();
            }
        });

        btnqueryhistory.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
               final DatePicker datePicker = new DatePicker(AccInfoActivity.this);
                 datePicker.setCalendarViewShown(false);
               // ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
                new  AlertDialog.Builder(AccInfoActivity.this)
                        .setTitle("选择要查询的月份" )
                        .setView(datePicker)
                        .setPositiveButton("确定" , new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                                // TODO Auto-generated method stub
                                final String year = Integer.toString(datePicker.getYear());
                                final String month = Integer.toString(datePicker.getMonth() + 1);

                                progressDialog = new ProgressDialog(AccInfoActivity.this);
                                progressDialog.setMessage("正在查询...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                new Thread(new Runnable()
                                {

                                    @Override
                                    public void run()
                                    {
                                        // TODO Auto-generated method stub
                                        QueryListActivity.title = year + "年" + month + "月历史交易记录";
                                        try
                                        {
                                            QueryListActivity.dataList = login.getHistoryQueryData(year, month);
                                            handler.obtainMessage(MSG_QUERYCOMPLETE).sendToTarget();
                                        } catch (Exception e)
                                        {
                                            // TODO Auto-generated catch block
                                            handler.obtainMessage(MSG_QUERYERROR).sendToTarget();
                                        }
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("取消" ,  null )
                        .show();

            }
        });

        btnlossreport.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Toast.makeText(AccInfoActivity.this, "请到相关工作地点或者拨打电话挂失^_^", Toast.LENGTH_LONG).show();
            }
        });
    }
}
