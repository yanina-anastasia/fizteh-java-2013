package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class PutCommand extends AbstractCommand {
    public PutCommand() {
        super("put", 2);
    }

    @Override
    public void execute(String[] input, AbstractFileMap.ShellState state) throws IOException {
        String keyValue = AbstractFileMap.getMap().put(input[0], input[1]);

        if (keyValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite\n" + keyValue);
        }
    }
}
