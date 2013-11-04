package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.IOException;
import java.util.Vector;

public interface CommandInterface {

    public abstract void execute(Vector<String> args) throws IOException, IllegalArgumentException, IllegalStateException;

}
