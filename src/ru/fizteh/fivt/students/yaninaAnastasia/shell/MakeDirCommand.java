package ru.fizteh.fivt.students.yaninaAnastasia.shell.Commands;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.ShellState;

import java.io.File;
import java.io.IOException;
import java.lang.System;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MakeDirCommand extends Command {
    public boolean exec(String[] args, ShellState curState) throws IOException {
        if (args.length != 1) {
            System.out.println("Invalid number of arguments");
            return false;
        }
        Path curPath = Paths.get(args[0]);
        if (curState.workingDirectory != null && !curPath.isAbsolute()) {
            curPath = Paths.get(curState.workingDirectory).resolve(curPath);
        }
        File directoryFile = curPath.normalize().toFile();
        if (directoryFile.exists()) {
            System.err.println("Error with making a directory");
            return false;
        }
        directoryFile.mkdir();
        return true;
    }

    public String getCmd() {
        return "mkdir";
    }
}