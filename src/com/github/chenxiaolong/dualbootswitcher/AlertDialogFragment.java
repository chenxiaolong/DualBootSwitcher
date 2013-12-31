package com.github.chenxiaolong.dualbootswitcher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class AlertDialogFragment extends DialogFragment {
    public static final String TAG = "alert_dialog";

    private String mTitle = "";
    private String mMessage = "";

    private String mNegativeText = "";
    private String mPositiveText = "";

    public DialogInterface.OnClickListener mNegative = null;
    public DialogInterface.OnClickListener mPositive = null;

    public static AlertDialogFragment newInstance() {
        AlertDialogFragment f = new AlertDialogFragment();

        return f;
    }

    public static AlertDialogFragment newInstance(String title, String message,
            String negativeText, DialogInterface.OnClickListener negative,
            String positiveText, DialogInterface.OnClickListener positive) {
        AlertDialogFragment f = newInstance();

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("negativeText", negativeText);
        args.putString("positiveText", positiveText);

        // Not the best way
        f.mNegative = negative;
        f.mPositive = positive;

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
            mNegativeText = getArguments().getString("negativeText");
            mPositiveText = getArguments().getString("positiveText");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNegativeButton(mNegativeText, mNegative);
        builder.setPositiveButton(mPositiveText, mPositive);
        builder.setTitle(mTitle);
        builder.setMessage(mMessage);

        return builder.create();
    }

    // Work around bug: http://stackoverflow.com/a/15444485/1064977
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    public void cancel() {
        getDialog().cancel();
    }
}
