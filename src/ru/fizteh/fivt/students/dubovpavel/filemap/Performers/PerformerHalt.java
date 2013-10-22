package ru.fizteh.fivt.students.dubovpavel.filemap.performers;

import ru.fizteh.fivt.students.dubovpavel.filemap.DispatcherFileMap;
import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;

public class PerformerHalt extends PerformerFileMap {
    public boolean pertains(Command command) {
        return command.getHeader().equals("halt") && command.argumentsCount() == 0;
    }

    public void execute(DispatcherFileMap dispatcher, Command command) throws PerformerException {
        dispatcher.shutDown();
    }
}
