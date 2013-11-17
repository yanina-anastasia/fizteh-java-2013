package ru.fizteh.fivt.students.fedoseev.common;

import java.io.IOException;
import java.util.Map;

public interface Frame {
    interface Command<State> {
        String getCmdName();

        int getArgsCount();

        void execute(String[] input, State state) throws IOException, InterruptedException;
    }

    Map<String, AbstractCommand> getCommands();

    void runCommands(String cmd, int end) throws IOException, InterruptedException;

    void BatchMode(String[] args);

    void InteractiveMode() throws InterruptedException;
}
