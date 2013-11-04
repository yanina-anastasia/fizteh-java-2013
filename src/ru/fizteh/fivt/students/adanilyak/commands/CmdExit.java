package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.students.adanilyak.modernfilemap.FileMapGlobalState;
import ru.fizteh.fivt.students.adanilyak.multifilehashmap.MultiFileDataBaseGlobalState;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableDataBaseGlobalState;

import java.io.IOException;
import java.util.List;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 0:27
 */
public class CmdExit implements Cmd {
    private final String name = "exit";
    private final int amArgs = 0;
    private StoreableDataBaseGlobalState storeableWorkState = null;
    private FileMapGlobalState multifileWorkState = null;

    public CmdExit(StoreableDataBaseGlobalState stateTable) {
        storeableWorkState = stateTable;
    }

    public CmdExit(FileMapGlobalState stateTable) {
        multifileWorkState = stateTable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAmArgs() {
        return amArgs;
    }

    @Override
    public void work(List<String> args) throws IOException {
        if (multifileWorkState == null) {
            if (storeableWorkState.autoCommitOnExit) {
                if (storeableWorkState.currentTable != null) {
                    storeableWorkState.currentTable.commit();
                }
            }
            System.exit(0);
        } else {
            if (multifileWorkState.autoCommitOnExit) {
                if (multifileWorkState.currentTable != null) {
                    multifileWorkState.currentTable.commit();
                }
            }
            System.exit(0);
        }
    }
}
