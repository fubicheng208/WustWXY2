package com.wustwxy2.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wustwxy2.R;
import com.wustwxy2.bean.Course;
import com.wustwxy2.models.JwInfoDB;

/**
 * Created by LvQingYang
 * on 2016/10/12.
 */

public class CourseInfoFragment extends DialogFragment {

    private static final String ARG_COURSE = "ARG_COURSE";
    private TextView mTitleTextView;
    private TextView mNameTextTviw,mRoomTextTviw,mTeacherTextTviw,mJsTextTviw,mZsTextTviw;
    private AppCompatCheckBox mBox;
    private TextView mDeleteTextView;
    private Course mCourse;

    public static CourseInfoFragment newInstance(Course c) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_COURSE,c);
        CourseInfoFragment fragment = new CourseInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCourse= (Course) getArguments().getSerializable(ARG_COURSE);

        View v=LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_course_more_info,null);
        mTitleTextView=(TextView)v.findViewById(R.id.title_text_view);
        mNameTextTviw=(TextView)v.findViewById(R.id.name_text_view);
        mRoomTextTviw=(TextView)v.findViewById(R.id.room_text_view);
        mTeacherTextTviw=(TextView)v.findViewById(R.id.teacher_text_view);
        mJsTextTviw=(TextView)v.findViewById(R.id.js_text_view);
        mZsTextTviw=(TextView)v.findViewById(R.id.zs_text_view);
        mDeleteTextView = (TextView) v.findViewById(R.id.delete_text_view);
        mDeleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JwInfoDB.getJwInfoDB(getActivity()).deleteCourse(mCourse.getId());
                dismiss();
                AddCourseFragment.UpdateCourseListener listener= (AddCourseFragment.UpdateCourseListener) getActivity();
                listener.onUpdateCourse();
            }
        });
        mBox=(AppCompatCheckBox) v.findViewById(R.id.is_delete_check_box);
        mBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBox.isChecked()) {
                    mDeleteTextView.setTextColor(getResources().getColor(R.color.delete));
                    mDeleteTextView.setEnabled(true);
                }else {
                    mDeleteTextView.setTextColor(getResources().getColor(R.color.delete_unenable));
                    mDeleteTextView.setEnabled(false);
                }
            }
        });

        mTitleTextView.setText(mCourse.getName());
        mNameTextTviw.setText(mCourse.getName());
        mRoomTextTviw .setText(mCourse.getRoom());
        mTeacherTextTviw.setText(mCourse.getTeach());
        String week="";
        switch (mCourse.getWeek()) {
            case 1:  week="一";  break;
            case 2:  week="二";  break;
            case 3:  week="三";  break;
            case 4:  week="四";  break;
            case 5:  week="五";  break;
            case 6:  week="六";  break;
            case 7:  week="日";  break;
        }
        mJsTextTviw.setText("周"+week+mCourse.getStart()+"-"
                +(mCourse.getStart()+mCourse.getStep()-1));
        mZsTextTviw.setText(mCourse.getStartZc()+"-"+mCourse.getEndZc()+"周");

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }
}
