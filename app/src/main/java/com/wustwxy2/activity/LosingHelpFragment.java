package com.wustwxy2.activity;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wustwxy2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LosingHelpFragment extends DialogFragment {


    public static LosingHelpFragment newInstance() {

        Bundle args = new Bundle();
        LosingHelpFragment fragment = new LosingHelpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v=LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_losing_help,null);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok,null)
                .create();
    }

}
