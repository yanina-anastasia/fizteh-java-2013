package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class FileMapRemoveCommand extends AbstractCommand<FileMapState> {
    public FileMapRemoveCommand() {
        super("remove", 1);
    }

    @Override
    public void execute(String[] input, FileMapState state) throws IOException {
        String key = AbstractFileMap.getContent().remove(input[0]);

        if (key == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
