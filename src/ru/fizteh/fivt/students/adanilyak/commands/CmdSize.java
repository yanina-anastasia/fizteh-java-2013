package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.students.adanilyak.multifilehashmap.MultiFileDataBaseGlobalState;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableDataBaseGlobalState;

import java.io.IOException;
import java.util.List;

/**
 * User: Alexander
 * Date: 28.10.13
 * Time: 16:13
 */
public class CmdSize implements Cmd {
    private final String name = "size";
    private final int amArgs = 0;
    private StoreableDataBaseGlobalState storeableWorkState = null;
    private MultiFileDataBaseGlobalState multifileWorkState = null;

    public CmdSize(StoreableDataBaseGlobalState dataBaseState) {
        storeableWorkState = dataBaseState;
    }

    public CmdSize(MultiFileDataBaseGlobalState dataBaseState) {
        multifileWorkState = dataBaseState;
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
            if (storeableWorkState.currentTable != null) {
                System.out.println(storeableWorkState.currentTable.size());
            } else {
                System.out.println("no table");
            }
        } else {
            if (multifileWorkState.currentTable != null) {
                System.out.println(multifileWorkState.currentTable.size());
            } else {
                System.out.println("no table");
            }
        }
    }
}
