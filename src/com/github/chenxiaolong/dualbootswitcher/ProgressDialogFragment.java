package com.github.chenxiaolong.dualbootswitcher;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

public class ProgressDialogFragment extends DialogFragment {
    public static final String TAG = "progress_dialog";

    private ProgressDialog mDialog;
    private String mTitle = "";
    private String mMessage = "";

    public static ProgressDialogFragment newInstance() {
        ProgressDialogFragment f = new ProgressDialogFragment();

        return f;
    }

    public static ProgressDialogFragment newInstance(String title,
            String message) {
        ProgressDialogFragment f = newInstance();

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);

        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mTitle = getArguments().getString("title");
            mMessage = getArguments().getString("message");
        }

        mDialog = new ProgressDialog(getActivity());
        mDialog.setIndeterminate(true);
        mDialog.setTitle(mTitle);
        mDialog.setMessage(mMessage);

        return mDialog;
    }

    // Work around bug: http://stackoverflow.com/a/15444485/1064977
    @Override
    public void onDestroyView() {
        mDialog = null;

        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    public void cancel() {
        getDialog().cancel();
    }
}
