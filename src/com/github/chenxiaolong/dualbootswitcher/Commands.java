package com.github.chenxiaolong.dualbootswitcher;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;

public class Commands {
    public static class CommandResult {
        public String output;
        public int exitCode;
    }

    public static CommandResult runCommand(String command_string)
            throws TimeoutException, RootDeniedException, IOException,
            InterruptedException {
        if (!requestRootAccess()) {
            throw new RootDeniedException("Root access denied");
        }

        CommandCapture command = new CommandCapture(0, command_string);
        Command process = RootTools.getShell(true).add(command);
        process.waitForFinish();

        CommandResult result = new CommandResult();
        result.output = command.toString();
        result.exitCode = process.exitCode();

        return result;
    }

    private static boolean requestRootAccess() {
        // if (!RootTools.isRootAvailable()) {
        //     return ERROR_NO_ROOT;
        // }
        if (!RootTools.isAccessGiven()) {
            return false;
        }
        return true;
    }
}
