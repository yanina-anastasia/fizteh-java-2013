package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.students.adanilyak.multifilehashmap.MultiFileDataBaseGlobalState;
import ru.fizteh.fivt.students.adanilyak.multifilehashmap.MultiFileTable;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableDataBaseGlobalState;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTable;

import java.io.IOException;
import java.util.List;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:19
 */
public class CmdUse implements Cmd {
    private final String name = "use";
    private final int amArgs = 1;
    private StoreableDataBaseGlobalState storeableWorkState = null;
    private MultiFileDataBaseGlobalState multifileWorkState = null;

    public CmdUse(StoreableDataBaseGlobalState dataBaseState) {
        storeableWorkState = dataBaseState;
    }

    public CmdUse(MultiFileDataBaseGlobalState dataBaseState) {
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
            if (storeableWorkState.getTable(useTableName) == null) {
                System.err.println(useTableName + " not exists");
            } else {
                if (storeableWorkState.getCurrentTable() != null) {
                    int amChanges = ((StoreableTable) storeableWorkState.currentTable).getAmountOfChanges();
                    if (amChanges != 0) {
                        System.err.println(amChanges + " unsaved changes");
                        return;
                    }
                }
                storeableWorkState.setCurrentTable(useTableName);
                System.out.println("using " + useTableName);
            }
        } else {
            String useTableName = args.get(1);
            if (multifileWorkState.getTable(useTableName) == null) {
                System.err.println(useTableName + " not exists");
            } else {
                if (multifileWorkState.getCurrentTable() != null) {
                    int amChanges = ((MultiFileTable)multifileWorkState.currentTable).getAmountOfChanges();
                    if (amChanges != 0) {
                        System.err.println(amChanges + " unsaved changes");
                        return;
                    }
                }
                multifileWorkState.setCurrentTable(useTableName);
                System.out.println("using " + useTableName);
            }
        }
    }
}
