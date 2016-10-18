package com.wustwxy2.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.wustwxy2.R;
import com.wustwxy2.bean.Course;
import com.wustwxy2.models.JwInfoDB;

/**
 * Created by LvQingYang
 * on 2016/10/12.
 */

public class AddCourseFragment extends DialogFragment {

    private static final String ARG_COURSE = "ARG_COURSE";
    private EditText mNameTextTviw,mRoomTextTviw,mTeacherTextTviw;
    private  EditText mJsTextTviw1,mJsTextTviw2,mJsTextTviw3,mZsTextTviw1,mZsTextTviw2;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public static AddCourseFragment newInstance() {

        Bundle args = new Bundle();
        AddCourseFragment fragment = new AddCourseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface UpdateCourseListener{
        void onUpdateCourse();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();

        View v=LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_add_course,null);
        mNameTextTviw=(EditText) v.findViewById(R.id.name_text_view);
        mRoomTextTviw=(EditText)v.findViewById(R.id.room_text_view);
        mTeacherTextTviw=(EditText)v.findViewById(R.id.teacher_text_view);
        mJsTextTviw1=(EditText)v.findViewById(R.id.js_text_view_1);
        mJsTextTviw2=(EditText)v.findViewById(R.id.js_text_view_2);
        mJsTextTviw3=(EditText)v.findViewById(R.id.js_text_view_3);
        mZsTextTviw1=(EditText)v.findViewById(R.id.zs_text_view_1);
        mZsTextTviw2=(EditText)v.findViewById(R.id.zs_text_view_2);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anIntrface, int i) {
                        String week,classStart,classEnd,startZc,endZc,skdd;
                        week=mJsTextTviw1.getText().toString();
                        classStart=mJsTextTviw2.getText().toString();
                        classEnd=mJsTextTviw3.getText().toString();
                        startZc=mZsTextTviw1.getText().toString();
                        endZc=mZsTextTviw2.getText().toString();
                        skdd=mRoomTextTviw.getText().toString();

                        if(week.isEmpty()||classStart.isEmpty()||classEnd.isEmpty()
                                ||startZc.isEmpty()||endZc.isEmpty()||skdd.isEmpty()){
                            Toast.makeText(getActivity(), "添加课程信息不完整", Toast.LENGTH_SHORT).show();
                        }else{
                            Course c=new Course();
                            c.setName(mNameTextTviw.getText().toString());
                            c.setRoom(skdd);
                            c.setTeach(mTeacherTextTviw.getText().toString());
                            c.setWeek(Integer.valueOf(week));
                            c.setStart(Integer.valueOf(classStart));
                            c.setStep(Integer.valueOf(classEnd)-Integer.valueOf(classStart)+1);
                            c.setStartZc(Integer.valueOf(startZc));
                            c.setEndZc(Integer.valueOf(endZc));
                            int classCode=mPreferences.getInt("classCode",0);
                            c.setClassCode(classCode);
                            mEditor.putInt("classCode",classCode+1);
                            mEditor.commit();
                            JwInfoDB.getJwInfoDB(getActivity()).saveCourse(c);
                            UpdateCourseListener listener= (UpdateCourseListener) getActivity();
                            listener.onUpdateCourse();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel,null)
                .create();
    }
}
