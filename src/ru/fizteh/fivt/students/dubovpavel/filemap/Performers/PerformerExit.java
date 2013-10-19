package ru.fizteh.fivt.students.dubovpavel.filemap.Performers;

import ru.fizteh.fivt.students.dubovpavel.filemap.Command;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseHandler;
import ru.fizteh.fivt.students.dubovpavel.filemap.Dispatcher;

public class PerformerExit extends Performer {
    public boolean pertains(Command command) {
        return command.getHeader().equals("exit");
    }

    public void execute(Dispatcher dispatcher, Command command) throws PerformerException {
        try {
            dispatcher.getDataBase().save();
            dispatcher.shutDown();
        } catch (DataBaseHandler.DataBaseException e) {
            throw new PerformerException(dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("Can not exit, use 'halt' if you really want to: %s", e.getMessage())));
        }
    }
}
