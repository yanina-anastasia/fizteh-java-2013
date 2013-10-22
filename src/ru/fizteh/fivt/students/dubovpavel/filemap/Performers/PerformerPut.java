package ru.fizteh.fivt.students.dubovpavel.filemap.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.filemap.DispatcherFileMap;
import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;

public class PerformerPut extends PerformerFileMap {
    public boolean pertains(Command command) {
        return command.getHeader().equals("put") && command.argumentsCount() == 2;
    }

    public void execute(DispatcherFileMap dispatcher, Command command) throws PerformerException {
        String old = dispatcher.getDataBase().put(command.getArgument(0), command.getArgument(1));
        if(old == null) {
            dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS, "new");
        } else {
            dispatcher.callbackWriter(Dispatcher.MessageType.WARNING, String.format("overwrite\n%s", old));
        }
    }
}
