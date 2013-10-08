package com.github.chenxiaolong.dualbootswitcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class DualBootUtils {
    public final static String TAG = "DualBootUtils";

    public static int backupKernel(int which) {
        String system_path = "/raw-system";

        try {
            int exitStatus;

            exitStatus = runCommand("ls " + system_path);
            if (exitStatus != 0) {
                Log.v(TAG, "/raw-system does not exist; using /system");
                system_path = system_path.replace("raw-system", "system");
                exitStatus = runCommand("ls " + system_path);
                if (exitStatus != 0) {
                    return SharedState.ERROR_DIRECTORY_NOT_FOUND;
                }
            }

            Log.v(TAG, "Trying to remount /system with read-write permissions");
            mountSystemReadWrite();

            String kernel_path = system_path + "/dual-kernels";
            exitStatus = runCommand("ls " + kernel_path);
            if (exitStatus != 0) {
                Log.v(TAG, kernel_path + " does not exist; creating it");
                exitStatus = runCommand("mkdir " + kernel_path);
                if (exitStatus != 0) {
                    Log.e(TAG, "Failed to create " + kernel_path);
                    return SharedState.ERROR_COMMAND_FAILED;
                }
            }

            String backup_path = "";
            if (which == SharedState.KERNEL_PRIMARY) {
                Log.v(TAG, "Backup up primary kernel");
                backup_path = kernel_path + "/primary.img";
            }
            else if (which == SharedState.KERNEL_SECONDARY) {
                Log.v(TAG, "Backup up secondary kernel");
                backup_path = kernel_path + "/secondary.img";
            }

            exitStatus = runCommand("dd if=" + SharedState.BOOT_PARTITION + " of=" + backup_path);

            if (exitStatus != 0) {
                Log.e(TAG, "Failed to backup to " + backup_path + " with dd");
                return SharedState.ERROR_COMMAND_FAILED;
            }

            Log.v(TAG, "Trying to remount /system with read-only permissions");
            mountSystemReadOnly();

            return SharedState.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            return SharedState.ERROR_OTHER;
        } catch (TimeoutException e) {
            e.printStackTrace();
            return SharedState.ERROR_TIMEOUT;
        } catch (RootDeniedException e) {
            e.printStackTrace();
            return SharedState.ERROR_ROOT_DENIED;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return SharedState.ERROR_OTHER;
        }
    }

    public static int writeKernel(int which) {
        String path = "";
        if (which == SharedState.KERNEL_PRIMARY) {
            path = "/raw-system/dual-kernels/primary.img";
        }
        else if (which == SharedState.KERNEL_SECONDARY) {
            path = "/raw-system/dual-kernels/secondary.img";
        }

        try {
            int exitStatus;

            exitStatus = runCommand("ls " + path);
            if (exitStatus != 0) {
                Log.v(TAG, path + " not found; trying /system");
                path = path.replace("raw-system", "system");
                exitStatus = runCommand("ls " + path);
                if (exitStatus != 0) {
                    Log.e(TAG, path + " not found");
                    return SharedState.ERROR_FILE_NOT_FOUND;
                }
            }

            exitStatus = runCommand("dd if=" + path + " of=" + SharedState.BOOT_PARTITION);

            if (exitStatus != 0) {
                Log.e(TAG, "Failed to write " + path + " with dd");
                return SharedState.ERROR_COMMAND_FAILED;
            }

            return SharedState.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            return SharedState.ERROR_OTHER;
        } catch (TimeoutException e) {
            e.printStackTrace();
            return SharedState.ERROR_TIMEOUT;
        } catch (RootDeniedException e) {
            e.printStackTrace();
            return SharedState.ERROR_ROOT_DENIED;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return SharedState.ERROR_OTHER;
        }
    }

    private static void mountSystemReadWrite() throws TimeoutException,
                                                      RootDeniedException,
                                                      IOException,
                                                      InterruptedException {
        int exitStatus = runCommand("ls /raw-system");
        if (exitStatus == 0) {
            runCommand("mount -o remount,rw /raw-system");
        }
        runCommand("mount -o remount,rw /system");
    }

    private static void mountSystemReadOnly() throws TimeoutException,
                                                     RootDeniedException,
                                                     IOException,
                                                     InterruptedException {
        int exitStatus = runCommand("ls /raw-system");
        if (exitStatus == 0) {
            runCommand("mount -o remount,ro /raw-system");
        }
        runCommand("mount -o remount,ro /system");
    }

    public static void reboot() {
        // Samsung's bootloader refuses to boot the new kernel if Android's
        // reboot button is pressed
        /*String command =
                "ps | grep '^u[0-9]*_' | awk '{print $2}' | xargs kill;" +
                "sync;" +
                "echo 1 > /proc/sys/kernel/sysrq;" +
                "echo s > /proc/sysrq-trigger;" +
                "echo b > /proc/sysrq-trigger";*/
        String command = "reboot";

        try {
            Log.v(TAG, "Running reboot command: \"" + command + "\"");
            runCommand(command);
        }
        catch (Exception e) {
        }
    }

    private static int runCommand(String command_string) throws TimeoutException,
                                                                RootDeniedException,
                                                                IOException,
                                                                InterruptedException {
        int status = requestRootAccess();
        if (status != SharedState.SUCCESS) {
            throw new RootDeniedException("Root access denied");
        }

        CommandCapture command = new CommandCapture(0, command_string);
        Command process = RootTools.getShell(true).add(command);
        process.waitForFinish();
        return process.exitCode();
    }

    private static int requestRootAccess() {
        //if (!RootTools.isRootAvailable()) {
        //    return ERROR_NO_ROOT;
        //}
        if (!RootTools.isAccessGiven()) {
            return SharedState.ERROR_ROOT_DENIED;
        }
        return SharedState.SUCCESS;
    }
}
