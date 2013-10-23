package ru.fizteh.fivt.students.abramova.shell;

import ru.fizteh.fivt.students.abramova.filemap.*;

public class CommandGetter {
    public static Command getCommand(String commandName, Status status) {
        if (!status.isFileMap()) {
            return getCommandShell(commandName);
        }
        if (status.isFileMap()) {
            return getCommandFileMap(commandName);
        }
        return null;
    }

    private static Command getCommandShell(String commandName) {
        if (commandName.equals("cd")) {
            return new ChangeDirectoryCommand("cd");
        }
        if (commandName.equals("rm")) {
            return new RemoveCommand("rm");
        }
        if (commandName.equals("cp")) {
            return new CopyToCommand("cp");
        }
        if (commandName.equals("mv")) {
            return new MoveToCommand("mv");
        }
        if (commandName.equals("dir")) {
            return new DirectoryCommand("dir");
        }
        if (commandName.equals("mkdir")) {
            return new MakeDirectoryCommand("mkdir");
        }
        if (commandName.equals("pwd")) {
            return new PrintWorkingDirectoryCommand("pwd");
        }
        if (commandName.equals("exit")) {
            return new ExitCommand("exit");
        }
        return null;
    }

    private static Command getCommandFileMap(String commandName) {
        if (commandName.equals("put")) {
            return new PutCommand("put");
        }
        if (commandName.equals("get")) {
            return new GetCommand("get");
        }
        if (commandName.equals("remove")) {
            return new RemoveFromMapCommand("remove");
        }
        if (commandName.equals("exit")) {
            return new ExitCommand("exit");
        }
        return null;
    }
}
