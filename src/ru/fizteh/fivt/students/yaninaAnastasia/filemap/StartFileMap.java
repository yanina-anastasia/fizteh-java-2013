package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;
import ru.fizteh.fivt.students.yaninaAnastasia.shell.Shell;

import java.io.IOException;
import java.util.ArrayList;

public class StartFileMap {
    public static void main(String[] args) {
        Shell shell = new Shell();
        DBState curState = new DBState();
        try {
            if (!OpenFile.open(curState)) {
                System.err.println("Error with opening file");
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("Error in IO");
            System.exit(1);
        }

        ArrayList<Command> cmdList = new ArrayList<Command>();
        Command cmd = new CommandPut();
        cmdList.add(cmd);
        cmd = new CommandGet();
        cmdList.add(cmd);
        cmd = new CommandRemove();
        cmdList.add(cmd);
        cmd = new CommandExit();
        cmdList.add(cmd);
        shell.fillHashMap(cmdList);
        if (args.length == 0) {
            shell.interActive();
        } else {
            shell.pocket(args);
        }
    }
}
