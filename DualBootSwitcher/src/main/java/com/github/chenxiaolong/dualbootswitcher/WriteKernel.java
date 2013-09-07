package com.github.chenxiaolong.dualbootswitcher;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.lang.Runnable;

public class WriteKernel extends AsyncTask<Integer, Void, Integer> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        SharedState.busyWritingKernel = true;
        SharedState.mProgressDialog.show();
        SharedState.mProgressDialogVisible = true;
        SharedState.mProgressDialog.setTitle("Dual Boot Switcher");
        SharedState.mProgressDialog.setMessage("Writing kernel...");
        SharedState.mProgressDialogTitle = "Dual Boot Switcher";
        SharedState.mProgressDialogText = "Writing kernel...";
    }

    @Override
    protected Integer doInBackground(Integer[] params) {
        if (params.length < 1) {
            return SharedState.ERROR_OTHER;
        }
        return DualBootUtils.writeKernel(params[0]);
    }

    @Override
    protected void onPostExecute(Integer result) {
        SharedState.busyWritingKernel = false;
        SharedState.mProgressDialogVisible = false;
        SharedState.mProgressDialog.dismiss();
        if (result == SharedState.SUCCESS) {
            //DialogUtils.showDialog("Now just click the reboot button in *this app* :)");
            DialogUtils.showDialog("Now just reboot :) (and make sure to unplug the USB cable)");
        }
        else {
            DialogUtils.handleReturnCode(result);
        }
    }
}
