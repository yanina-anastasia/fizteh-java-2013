package ru.fizteh.fivt.students.dubovpavel.shell2.Performers;

import ru.fizteh.fivt.students.dubovpavel.shell2.Command;
import ru.fizteh.fivt.students.dubovpavel.shell2.Dispatcher;

import java.io.File;

public class PerformerCreateDirectory extends Performer {
    public boolean pertains(Command command) {
        return command.getHeader().equals("mkdir") && command.argumentsCount() == 1;
    }

    public void execute(Dispatcher dispatcher, Command command) throws PerformerException {
        File directory = getCanonicalFile(command.getArgument(0));
        if(!directory.mkdirs()) {
            throw new PerformerException(dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("'%s'. mkdir: Can not create '%s'", command.getDescription(), directory.getPath())));
        }
    }
}
