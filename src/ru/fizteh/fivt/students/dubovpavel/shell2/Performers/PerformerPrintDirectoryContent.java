package ru.fizteh.fivt.students.dubovpavel.shell2.Performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;

import java.io.File;
import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;

public class PerformerPrintDirectoryContent extends PerformerShell {
    public boolean pertains(Command command) {
        return command.getHeader().equals("dir") && command.argumentsCount() == 0;
    }

    public void execute(Dispatcher dispatcher, Command command) throws PerformerException {
        File directory = getCanonicalFile(".");
        for(String entry: directory.list()) {
            dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS, entry);
        }
    }
}
