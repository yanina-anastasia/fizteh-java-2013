package ru.fizteh.fivt.students.chernigovsky.filemap;

import java.io.File;

public class StateProvider {
    private State currentState;
    private File dbDirectory;

    public StateProvider(File newDbDirectory) {
        dbDirectory = newDbDirectory;
    }

    public void changeCurrentState(State newState) {
        currentState = newState;
    }

    public State getCurrentState() {
        return currentState;
    }

    public File getDbDirectory() {
        return dbDirectory;
    }
}
