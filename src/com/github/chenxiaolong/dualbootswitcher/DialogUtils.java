package com.github.chenxiaolong.dualbootswitcher;

import android.app.AlertDialog;
import android.content.DialogInterface;

public class DialogUtils {
    public static void showDialog(String message) {
        SharedState.mSimpleDialogText = message;
        SharedState.mSimpleDialogVisible = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(SharedState.mActivity.get());
        builder.setTitle(R.string.app_name);
        builder.setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedState.mSimpleDialogVisible = false;
                        SharedState.mSimpleDialogText = "";
                    }
                });
        builder.create().show();
    }


    public static void handleReturnCode(int code) {
        switch (code) {
            case SharedState.SUCCESS:
                showDialog("Successfully completed");
                break;
            case SharedState.ERROR_NO_ROOT:
                showDialog("Root access not available");
                break;
            case SharedState.ERROR_ROOT_DENIED:
                showDialog("Root access denied");
                break;
            case SharedState.ERROR_FILE_NOT_FOUND:
                showDialog("File not found");
                break;
            case SharedState.ERROR_DIRECTORY_NOT_FOUND:
                showDialog("Directory not found");
                break;
            case SharedState.ERROR_TIMEOUT:
                showDialog("Timeout error");
                break;
            case SharedState.ERROR_OTHER:
                showDialog("Other error");
                break;
            case SharedState.ERROR_COMMAND_FAILED:
                showDialog("Failed to run command");
                break;
            default:
                showDialog("UNKNOWN CODE");
                break;
        }
    }
}
