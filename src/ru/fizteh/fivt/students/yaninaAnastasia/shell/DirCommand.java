package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.ShellState;

import java.io.File;
import java.io.IOException;

public class DirCommand extends Command {
    public final boolean exec(String[] args, ShellState curState) throws IOException {
        if (args.length != 0) {
            System.out.println("Invalid number of arguments");
            return false;
        }
        try {
            String [] listFile = new File(curState.workingDirectory).list();
            for (String child : listFile) {
                System.out.println(child);
            }
        } catch (Exception e) {
            System.out.println("Error with command dir");
            return false;
        }
        return true;
    }

    public String getCmd() {
        return "dir";
    }
}