package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.filemap.AbstractFileMap;
import ru.fizteh.fivt.students.fedoseev.filemap.FileMapRemoveCommand;
import ru.fizteh.fivt.students.fedoseev.filemap.FileMapState;

import java.io.IOException;

public class MultiFileHashMapRemoveCommand extends AbstractCommand<MultiFileHashMapState> {
    private FileMapRemoveCommand remove = new FileMapRemoveCommand();

    public MultiFileHashMapRemoveCommand() {
        super("remove", 1);
    }

    @Override
    public void execute(String[] input, MultiFileHashMapState state) throws IOException {
        MultiFileHashMapTable table = state.getCurTable();

        if (table == null) {
            System.out.println("no table");
        } else {
            AbstractMultiFileHashMap.readTable(table);

            AbstractFileMap.setContent(table.getMapContent());

            remove.execute(input, new FileMapState());

            table.setMapContent(AbstractFileMap.getContent());
        }
    }
}
