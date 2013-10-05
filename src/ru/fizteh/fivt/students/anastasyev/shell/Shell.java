package ru.fizteh.fivt.students.anastasyev.shell;

import java.io.File;
import java.util.Vector;

public class Shell {
    public static File userDir;
    public Vector<Command> commands;

    public Shell() {
        Shell.userDir = new File(System.getProperty("user.dir"));
        commands = new Vector<>();
        commands.add(new CdCommand());
        commands.add(new CpCommand());
        commands.add(new DirCommand());
        commands.add(new MkdirCommand());
        commands.add(new MvCommand());
        commands.add(new PwdCommand());
        commands.add(new RmCommand());
        commands.add(new ExitCommand());
    }

    public void addCommand(Command command) {
        commands.add(command);
    }
}