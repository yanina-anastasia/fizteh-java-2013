package ru.fizteh.fivt.students.vishnevskiy.shell;

import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.util.Arrays;
import ru.fizteh.fivt.students.vishnevskiy.shell.сommands.*;

public class CommandsOperator {
    private Map<String, Command> commandsTable = new HashMap<String, Command>();
    private FileSystemOperator fileSystemOperator;

    private void loadClasses() {
        Cd cd = new Cd();
        commandsTable.put(cd.getName(), cd);
        Cp cp = new Cp();
        commandsTable.put(cp.getName(), cp);
        Dir dir = new Dir();
        commandsTable.put(dir.getName(), dir);
        Exit exit = new Exit();
        commandsTable.put(exit.getName(), exit);
        MkDir mkdir = new MkDir();
        commandsTable.put(mkdir.getName(), mkdir);
        Mv mv = new Mv();
        commandsTable.put(mv.getName(), mv);
        Pwd pwd = new Pwd();
        commandsTable.put(pwd.getName(), pwd);
        Rm rm = new Rm();
        commandsTable.put(rm.getName(), rm);
    }

    public CommandsOperator() {
        loadClasses();
        fileSystemOperator = new FileSystemOperator(".");
    }

    public int runCommand(String line) {
        try {
            line = line.replaceAll("\\s+", " ").trim();
            String[] commandAndArgs = line.split(" ");
            String commandName = commandAndArgs[0];
            String[] args = Arrays.copyOfRange(commandAndArgs, 1, commandAndArgs.length);
            Command command = commandsTable.get(commandName);
            if (command == null) {
                if (commandName.equals("")) {
                    return 0;
                } else {
                    throw new ShellException(commandName + ": command not found");
                }

            }
            command.execute(fileSystemOperator, args);
        } catch (ShellException e) {
            System.err.println(e.getMessage());
            return 1;
        }
        return 0;
    }
}
