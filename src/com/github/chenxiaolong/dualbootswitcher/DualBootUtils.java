package com.github.chenxiaolong.dualbootswitcher;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import android.util.Log;

import com.stericson.RootTools.exceptions.RootDeniedException;

public class DualBootUtils {
    public static final String TAG = "DualBootUtils";
    public static final String BOOT_PARTITION = "/dev/block/platform/msm_sdcc.1/by-name/boot";
    // Can't use Environment.getExternalStorageDirectory() because the path is
    // different in the root environment
    public static final String KERNEL_PATH_ROOT = "/raw-data/media/0/MultiKernels";

    public static void writeKernel(String rom) throws Exception {
        String rom_id = RomDetector.getId(rom);

        String[] paths = new String[] { KERNEL_PATH_ROOT,
                KERNEL_PATH_ROOT.replace("raw-data", "data"),
                "/raw-system/dual-kernels", "/system/dual-kernels" };

        String kernel = null;
        for (String path : paths) {
            String temp = path + File.separator + rom_id + ".img";
            Commands.CommandResult result = Commands.runCommand("ls " + temp);
            if (result.exitCode != 0) {
                Log.e(TAG, temp + " not found");
                continue;
            }
            kernel = temp;
            break;
        }

        if (kernel == null) {
            throw new Exception("The kernel for " + rom + " was not found");
        }

        Commands.CommandResult result = Commands.runCommand("dd if=" + kernel
                + " of=" + BOOT_PARTITION);
        if (result.exitCode != 0) {
            Log.e(TAG, "Failed to write " + kernel + " with dd");
            throw new Exception("Failed to write " + kernel + " with dd");
        }
    }

    public static void backupKernel(String rom) throws Exception {
        String rom_id = RomDetector.getId(rom);

        String kernel_path = KERNEL_PATH_ROOT;
        Commands.CommandResult result = Commands.runCommand("ls /raw-data");
        if (result.exitCode != 0) {
            kernel_path = kernel_path.replace("raw-data", "data");
        }
        Commands.runCommand("mkdir -p " + kernel_path);
        String kernel = kernel_path + File.separator + rom_id + ".img";

        Log.v(TAG, "Trying to remount /system with read-write permissions");
        mountSystemReadWrite();

        Log.v(TAG, "Backing up " + rom_id + " kernel");

        result = Commands.runCommand("dd if=" + BOOT_PARTITION + " of="
                + kernel);

        if (result.exitCode != 0) {
            Log.e(TAG, "Failed to backup to " + kernel + " with dd");
            throw new Exception("Failed to backup to " + kernel + " with dd");
        }

        Log.v(TAG, "Trying to remount /system with read-only permissions");
        mountSystemReadOnly();

        Log.v(TAG, "Fixing permissions");
        Commands.runCommand("chmod -R 775 " + kernel_path);
        Commands.runCommand("chown -R media_rw:media_rw " + kernel_path);
    }

    private static void mountSystemReadWrite() throws TimeoutException,
            RootDeniedException, IOException, InterruptedException {
        Commands.CommandResult result = Commands.runCommand("ls /raw-system");
        String system;
        if (result.exitCode == 0) {
            system = "/raw-system";
        } else {
            system = "/system";
        }
        Commands.runCommand("mount -o remount,rw " + system);
    }

    private static void mountSystemReadOnly() throws TimeoutException,
            RootDeniedException, IOException, InterruptedException {
        Commands.CommandResult result = Commands.runCommand("ls /raw-system");
        String system;
        if (result.exitCode == 0) {
            system = "/raw-system";
        } else {
            system = "/system";
        }
        Commands.runCommand("mount -o remount,ro " + system);
    }
}
