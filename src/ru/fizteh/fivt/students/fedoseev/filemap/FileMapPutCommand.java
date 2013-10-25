package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class FileMapPutCommand extends AbstractCommand<FileMapState> {
    public FileMapPutCommand() {
        super("put", 2);
    }

    @Override
    public void execute(String[] input, FileMapState state) throws IOException {
        String keyValue = AbstractFileMap.getContent().put(input[0], input[1]);

        if (keyValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite\n" + keyValue);
        }
    }
}
