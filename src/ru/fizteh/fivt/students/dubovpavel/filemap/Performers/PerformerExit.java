package ru.fizteh.fivt.students.dubovpavel.filemap.performers;

import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseHandler;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.filemap.DispatcherFileMap;
import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;

public class PerformerExit extends PerformerFileMap {
    public boolean pertains(Command command) {
        return command.getHeader().equals("exit");
    }

    public void execute(DispatcherFileMap dispatcher, Command command) throws PerformerException {
        try {
            dispatcher.getDataBase().save();
            dispatcher.shutDown();
        } catch (DataBaseHandler.DataBaseException e) {
            throw new PerformerException(dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("Can not exit, use 'halt' if you really want to: %s", e.getMessage())));
        }
    }
}
