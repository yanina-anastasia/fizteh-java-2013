package ru.fizteh.fivt.students.kochetovnicolai.shell;

interface Executable {

    boolean execute(String[] args);

    String name();

    int argumentsNumber();

}
