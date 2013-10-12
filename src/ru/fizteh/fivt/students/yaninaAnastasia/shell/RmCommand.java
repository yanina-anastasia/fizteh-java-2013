package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.ShellState;

import java.io.File;
import java.io.IOException;
import java.lang.System;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RmCommand extends Command {
    public boolean exec(String[] args, ShellState curState) throws IOException {
        if (args.length > 1) {
            System.err.println("Invalid arguments");
            return false;
        }
        Path filePath = Paths.get(args[0]);
        if (curState.workingDirectory != null && !filePath.isAbsolute()) {
            filePath = Paths.get(curState.workingDirectory).resolve(filePath);
        }
        File myFile = filePath.toFile();
        if (!myFile.isDirectory()) {
            System.err.println("It is not a directory");
            return false;
        }

        File[] files = myFile.listFiles();
        String[] path = new String[1];
        if (files != null) {
            for (File itFile : files) {
                path[0] = itFile.toString();
                exec(path, curState);
            }
        }
        if (!myFile.delete()) {
            System.err.println("Error with deleting");
            return false;
        }
        return true;
    }

    public String getCmd() {
        return "rm";
    }
}