package ru.fizteh.fivt.students.dobrinevski.shell;

import java.util.HashMap;

public class Main {
    private static HashMap<String, Command> cmdMap = new HashMap<String, Command>();

    static {
        cmdMap.put("cd", new ShellCommands.Cd());
        cmdMap.put("mkdir", new ShellCommands.Mkdir());
        cmdMap.put("pwd", new ShellCommands.Pwd());
        cmdMap.put("remove", new ShellCommands.Remove());
        cmdMap.put("copy", new ShellCommands.Copy());
        cmdMap.put("move", new ShellCommands.Move());
        cmdMap.put("dir", new ShellCommands.Dir());
        cmdMap.put("exit", new ShellCommands.Exit());
    }

    public static void main(String[] args) {
        Shell sl = new Shell(cmdMap);
        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(arg).append(' ');
            }
            try {
                sl.executeCommands(builder.toString());
            } catch (Exception e) {
                System.err.println(e);
                System.exit(1);
            }
        } else {
            sl.iMode();
        }
    }
}