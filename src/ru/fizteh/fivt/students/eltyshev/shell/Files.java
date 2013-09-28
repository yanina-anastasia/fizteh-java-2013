package ru.fizteh.fivt.students.eltyshev.shell;

public class Files {
    private static Files ourInstance = new Files();

    public static Files getInstance() {
        return ourInstance;
    }

    private Files() {
    }
}
