package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.IOException;

public abstract class AbstractCommand implements Command {
    private String cmdName;
    private Integer argsCount;

    public AbstractCommand(String cmdName, Integer argsCount) {
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
