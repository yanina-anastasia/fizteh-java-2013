package ru.fizteh.fivt.students.dubovpavel.filemap.Performers;

import ru.fizteh.fivt.students.dubovpavel.filemap.Command;
import ru.fizteh.fivt.students.dubovpavel.filemap.Dispatcher;

public class PerformerHalt extends Performer {
    public boolean pertains(Command command) {
        return command.getHeader().equals("halt") && command.argumentsCount() == 0;
    }

    public void execute(Dispatcher dispatcher, Command command) throws PerformerException {
        dispatcher.shutDown();
    }
}
