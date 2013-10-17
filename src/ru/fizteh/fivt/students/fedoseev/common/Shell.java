package ru.fizteh.fivt.students.fedoseev.common;

import java.io.IOException;
import java.util.Map;

public interface Shell {
    interface Command {
        String getCmdName();

        int getArgsCount();

        void execute(String[] input, Abstract.ShellState state) throws IOException;
    }

    Map<String, AbstractCommand> getCommands();

    void run() throws IOException, InterruptedException;

    void runCommands(String cmd, int end) throws IOException;
}
