package ru.fizteh.fivt.students.dubovpavel.shell2.Performers;

import ru.fizteh.fivt.students.dubovpavel.shell2.Command;
import ru.fizteh.fivt.students.dubovpavel.shell2.Dispatcher;

public class PerformerExit extends Performer {
    public boolean pertains(Command command) {
        return command.getHeader().equals("exit");
    }

    public void execute(Dispatcher dispatcher, Command command) throws PerformerException {
        dispatcher.shutDown();
    }
}
