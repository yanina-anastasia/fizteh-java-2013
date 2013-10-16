package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.utilities.AbstractCommand;

import java.io.IOException;

public class RemoveCommand extends AbstractCommand {
    public RemoveCommand() {
        super("remove", 1);
    }

    @Override
    public void execute(String[] input, AbstractFileMap.ShellState state) throws IOException {
        String key = AbstractFileMap.getMap().remove(input[0]);

        if (key == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
