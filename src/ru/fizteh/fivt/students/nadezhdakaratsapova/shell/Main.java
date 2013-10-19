package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.util.Arrays;


public class Main {

    public static void main(String[] args) {
        Shell shell = new Shell();
        shell.addCommand(new CdCommand());
        shell.addCommand(new CpCommand());
        shell.addCommand(new DirCommand());
        shell.addCommand(new ExitCommand());
        shell.addCommand(new MkdirCommand());
        shell.addCommand(new MvCommand());
        shell.addCommand(new PwdCommand());
        shell.addCommand(new RmCommand());
        if (args.length == 0) {
            shell.interactiveMode();
        } else {
            String arguments = StringMethods.join(Arrays.asList(args), " ");
            shell.batchMode(arguments);
        }

    }
}
