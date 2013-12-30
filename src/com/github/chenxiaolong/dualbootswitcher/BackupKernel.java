package com.github.chenxiaolong.dualbootswitcher;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.lang.Runnable;

public class BackupKernel extends AsyncTask<Integer, Void, Integer> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        SharedState.busyBackingUpKernel = true;
        SharedState.mProgressDialog.show();
        SharedState.mProgressDialogVisible = true;
        SharedState.mProgressDialog.setTitle("Dual Boot Switcher");
        SharedState.mProgressDialog.setMessage("Backing up kernel...");
        SharedState.mProgressDialogTitle = "Dual Boot Switcher";
        SharedState.mProgressDialogText = "Backing up kernel...";
    }

    @Override
    protected Integer doInBackground(Integer[] params) {
        if (params.length < 1) {
            return SharedState.ERROR_OTHER;
        }
        return DualBootUtils.backupKernel(params[0]);
    }

    @Override
    protected void onPostExecute(Integer result) {
        SharedState.busyBackingUpKernel = false;
        SharedState.mProgressDialogVisible = false;
        SharedState.mProgressDialog.dismiss();
        DialogUtils.handleReturnCode(result);
    }
}
