package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.students.adanilyak.multifilehashmap.MultiFileDataBaseGlobalState;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableDataBaseGlobalState;
import ru.fizteh.fivt.students.adanilyak.tools.CheckOnCorrect;

import java.io.IOException;
import java.util.List;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:19
 */
public class CmdDrop implements Cmd {
    private final String name = "drop";
    private final int amArgs = 1;
    private StoreableDataBaseGlobalState storeableWorkState = null;
    private MultiFileDataBaseGlobalState multifileWorkState = null;

    public CmdDrop(StoreableDataBaseGlobalState dataBaseState) {
        storeableWorkState = dataBaseState;
    }

    public CmdDrop(MultiFileDataBaseGlobalState dataBaseState) {
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
            String useTableName = args.get(1);
            if (!CheckOnCorrect.goodArg(useTableName)) {
                throw new IllegalArgumentException("Bad table name");
            }
            if (storeableWorkState.getTable(useTableName) == null) {
                System.err.println(useTableName + " not exists");
                throw new IllegalStateException(useTableName + " not exists");
            } else {
                storeableWorkState.removeTable(useTableName);
                System.out.println("dropped");
            }
        } else {
            String useTableName = args.get(1);
            if (!CheckOnCorrect.goodArg(useTableName)) {
                throw new IllegalArgumentException("Bad table name");
            }
            if (multifileWorkState.getTable(useTableName) == null) {
                System.err.println(useTableName + " not exists");
                throw new IllegalStateException(useTableName + " not exists");
            } else {
                multifileWorkState.removeTable(useTableName);
                System.out.println("dropped");
            }
        }
    }
}
