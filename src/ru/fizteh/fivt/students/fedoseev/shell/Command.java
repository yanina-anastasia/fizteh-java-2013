package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.IOException;

public abstract class Command {
    private String cmdName;
    private Integer argsCount;

    public Command(String cmdName, Integer argsCount) {
        this.cmdName = cmdName;
        this.argsCount = argsCount;
    }

    public String getCmdName() {
        return cmdName;
    }

    public Integer getArgsCount() {
        return argsCount;
    }

    public abstract void execute(String[] input, Shell.ShellState state) throws IOException;
}
