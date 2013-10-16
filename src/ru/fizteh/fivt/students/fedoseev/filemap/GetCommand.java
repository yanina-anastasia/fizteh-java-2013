package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.utilities.AbstractCommand;

import java.io.IOException;

public class GetCommand extends AbstractCommand {
    public GetCommand() {
        super("get", 1);
    }

    @Override
    public void execute(String[] input, AbstractFileMap.ShellState state) throws IOException {
        String value = AbstractFileMap.getMap().get(input[0]);

        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found\n" + value);
        }
    }
}
