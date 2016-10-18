package com.wustwxy2.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.wustwxy2.R;

/**
 * Created by LvQingYang
 * on 2016/10/12.
 */

public class CourseHelpFragment extends DialogFragment {


    public static CourseHelpFragment newInstance() {

        Bundle args = new Bundle();
        CourseHelpFragment fragment = new CourseHelpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v=LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_course_help,null);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok,null)
                .create();
    }
}
