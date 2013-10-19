package ru.fizteh.fivt.students.dubovpavel.filemap.Performers;

import ru.fizteh.fivt.students.dubovpavel.filemap.Command;
import ru.fizteh.fivt.students.dubovpavel.filemap.Dispatcher;

public class PerformerRemove extends Performer {
    public boolean pertains(Command command) {
        return command.getHeader().equals("remove") && command.argumentsCount() == 1;
    }
    public void execute(Dispatcher dispatcher, Command command) throws PerformerException {
        String removed = dispatcher.getDataBase().remove(command.getArgument(0));
        if(removed == null) {
            dispatcher.callbackWriter(Dispatcher.MessageType.ERROR, "not found");
        } else {
            dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS, "removed");
        }
    }
}
