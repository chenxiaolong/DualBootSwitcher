package com.github.chenxiaolong.dualbootswitcher;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

import java.lang.ref.WeakReference;

public class SharedState {
    // Progress dialog
    public static ProgressDialog mProgressDialog = null;
    public static boolean mProgressDialogVisible = false;
    public static String mProgressDialogText = "";
    public static String mProgressDialogTitle = "";

    // Regular dialog
    public static boolean mSimpleDialogVisible = false;
    public static String mSimpleDialogText = "";


    public static boolean busyWritingKernel = false;
    public static boolean busyBackingUpKernel = false;
    public static boolean romChoiceExpanded = false;
    public static boolean backupKernelExpanded = false;

    public static WeakReference<MainActivity> mActivity = null;

    // Errors
    public final static int SUCCESS = 0;
    public final static int ERROR_NO_ROOT = 1;
    public final static int ERROR_ROOT_DENIED = 2;
    public final static int ERROR_FILE_NOT_FOUND = 3;
    public final static int ERROR_DIRECTORY_NOT_FOUND = 4;
    public final static int ERROR_TIMEOUT = 5;
    public final static int ERROR_OTHER = 6;
    public final static int ERROR_COMMAND_FAILED = 7;

    public final static int KERNEL_PRIMARY = 1;
    public final static int KERNEL_SECONDARY = 2;
    public final static String BOOT_PARTITION = "/dev/block/platform/msm_sdcc.1/by-name/boot";
}
