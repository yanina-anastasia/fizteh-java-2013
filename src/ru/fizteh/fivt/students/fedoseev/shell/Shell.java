package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.IOException;
import java.util.Map;

public interface Shell {
    interface Command {
        String getCmdName();

        int getArgsCount();

        void execute(String[] input, AbstractShell.ShellState state) throws IOException;
    }

    Map<String, AbstractCommand> setCommands();

    void run() throws IOException, InterruptedException;

    void runCommands(String cmd, int end) throws IOException;
}
