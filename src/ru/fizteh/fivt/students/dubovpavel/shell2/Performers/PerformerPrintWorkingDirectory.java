package ru.fizteh.fivt.students.dubovpavel.shell2.Performers;

import ru.fizteh.fivt.students.dubovpavel.shell2.Command;
import ru.fizteh.fivt.students.dubovpavel.shell2.Dispatcher;

public class PerformerPrintWorkingDirectory extends Performer {
    public boolean pertains(Command command) {
        return command.getHeader().equals("pwd") && command.argumentsCount() == 0;
    }

    public void execute(Dispatcher dispatcher, Command command) throws PerformerException {
        dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS, getCanonicalFile(".").getPath());
    }
}
