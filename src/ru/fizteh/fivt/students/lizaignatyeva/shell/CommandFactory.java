package ru.fizteh.fivt.students.lizaignatyeva.shell;

public class CommandFactory {
    //TODO: constructor from a map
    public Command makeCommand(String name) throws Exception{
        if (name.equals("cd")) {
            return new CdCommand();
        } else if (name.equals("dir")) {
            return new DirCommand();
        } else if (name.equals("pwd")) {
            return new PwdCommand();
        } else if (name.equals("mv")) {
            return new MvCommand();
        } else if (name.equals("cp")) {
            return new CpCommand();
        } else if (name.equals("mkdir")) {
            return new MkdirCommand();
        } else if (name.equals("rm")) {
            return new RmCommand();
        } else if (name.equals("exit")) {
            return new ExitCommand();
        } else {
            throw new IllegalArgumentException("command not found");
        }
    }
}
