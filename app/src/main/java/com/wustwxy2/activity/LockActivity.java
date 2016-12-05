package com.wustwxy2.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wustwxy2.R;
import com.wustwxy2.bean.Course;

public class LockActivity extends Activity {

    private static final String EXTRA_COURSE = "EXTRA_COURSE";
    private TextView mNameTextTviw,mRoomTextTviw,mTeacherTextTviw,mTitleTextView;
    private LinearLayout mLinearLayout;

    public static Intent newIntent(Context context, Course course){
        Intent intent=new Intent(context,LockActivity.class);
        intent.putExtra(EXTRA_COURSE,course);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                //| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_lock);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire();
            wl.release();
        }

        Course course= (Course) getIntent().getSerializableExtra(EXTRA_COURSE);
        mNameTextTviw=(TextView)findViewById(R.id.name_text_view);
        mRoomTextTviw=(TextView)findViewById(R.id.room_text_view);
        mTeacherTextTviw=(TextView)findViewById(R.id.teacher_text_view);
        mTitleTextView = (TextView)findViewById(R.id.title_text_view);
        mLinearLayout=(LinearLayout) findViewById(R.id.content_ll);

        mNameTextTviw.setText(course.getName());
        mRoomTextTviw .setText(course.getRoom());
        mTeacherTextTviw.setText(course.getTeach());
        mTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LockActivity.this, SearchTableActivity.class));
                finish();
            }
        });

    }
}
