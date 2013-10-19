package ru.fizteh.fivt.students.dubovpavel.filemap.Performers;

import ru.fizteh.fivt.students.dubovpavel.filemap.Command;
import ru.fizteh.fivt.students.dubovpavel.filemap.Dispatcher;

public class PerformerPut extends Performer {
    public boolean pertains(Command command) {
        return command.getHeader().equals("put") && command.argumentsCount() == 2;
    }

    public void execute(Dispatcher dispatcher, Command command) throws PerformerException {
        String old = dispatcher.getDataBase().put(command.getArgument(0), command.getArgument(1));
        if(old == null) {
            dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS, "new");
        } else {
            dispatcher.callbackWriter(Dispatcher.MessageType.WARNING, String.format("overwrite\n%s", old));
        }
    }
}
