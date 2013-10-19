package ru.fizteh.fivt.students.dubovpavel.filemap.Performers;

import ru.fizteh.fivt.students.dubovpavel.filemap.Command;
import ru.fizteh.fivt.students.dubovpavel.filemap.Dispatcher;

public class PerformerGet extends Performer {
    public boolean pertains(Command command) {
        return command.getHeader().equals("get") && command.argumentsCount() == 1;
    }
    public void execute(Dispatcher dispatcher, Command command) throws PerformerException {
        String value = dispatcher.getDataBase().get(command.getArgument(0));
        if(value == null) {
            dispatcher.callbackWriter(Dispatcher.MessageType.ERROR, "not found");
        } else {
            dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS, String.format("found\n%s", value));
        }
    }
}
