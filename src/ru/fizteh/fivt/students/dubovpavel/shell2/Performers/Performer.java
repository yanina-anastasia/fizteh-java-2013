package ru.fizteh.fivt.students.dubovpavel.shell2.Performers;

import ru.fizteh.fivt.students.dubovpavel.shell2.Command;
import ru.fizteh.fivt.students.dubovpavel.shell2.Dispatcher;

import java.io.File;
import java.io.IOException;

public abstract class Performer {
    public class PerformerException extends Exception {
        public PerformerException(String message) {
            super(message);
        }
    }

    protected File getCanonicalFile(String path) {
        try {
            return new File(path).getCanonicalFile();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage()); // This is very unlikely situation as far as I understand
        }
    }

    public abstract boolean pertains(Command command);
    public abstract void execute(Dispatcher dispatcher, Command command) throws PerformerException;
}
