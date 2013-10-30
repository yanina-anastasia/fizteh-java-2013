package ru.fizteh.fivt.students.vyatkina;

import java.io.PrintStream;
import java.util.concurrent.ExecutionException;

public interface Command {

    void execute (String[] args) throws ExecutionException;

    String getName ();

    int getArgumentCount ();

}
