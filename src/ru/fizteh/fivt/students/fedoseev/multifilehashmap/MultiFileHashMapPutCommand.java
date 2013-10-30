package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.filemap.AbstractFileMap;
import ru.fizteh.fivt.students.fedoseev.filemap.FileMapPutCommand;
import ru.fizteh.fivt.students.fedoseev.filemap.FileMapState;

import java.io.IOException;

public class MultiFileHashMapPutCommand extends AbstractCommand<MultiFileHashMapState> {
    private FileMapPutCommand put;

    public MultiFileHashMapPutCommand() {
        super("put", 2);

        put = new FileMapPutCommand();
    }

    @Override
    public void execute(String[] input, MultiFileHashMapState state) throws IOException {
        MultiFileHashMapTable table = state.getCurTable();

        if (table == null) {
            System.out.println("no table");
        } else {
            AbstractMultiFileHashMap.readTable(table);

            AbstractFileMap.setContent(table.getMapContent());

            put.execute(input, new FileMapState());

            table.setMapContent(AbstractFileMap.getContent());
        }
    }
}
