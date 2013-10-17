package ru.fizteh.fivt.students.fedoseev.common;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public abstract class Abstract implements Shell {
    public class ShellState {
        private File curState;

        public void setCurState(File dir) {
            curState = dir;
        }

        public File getCurState() {
            return curState;
        }
    }

    public abstract Map<String, AbstractCommand> getCommands();

    protected ShellState state = new ShellState();

    public Abstract(File dir) {
        state.setCurState(dir);
    }

    public abstract void run() throws IOException, InterruptedException;

    public abstract void runCommands(String cmd, int end) throws IOException;
}
