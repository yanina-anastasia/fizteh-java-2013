package ru.fizteh.fivt.students.kochetovnicolai.shell;

public interface Executable {

    boolean execute(String[] args);

    String name();

    int argumentsNumber();

}
