package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.students.adanilyak.multifilehashmap.MultiFileDataBaseGlobalState;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableDataBaseGlobalState;
import ru.fizteh.fivt.students.adanilyak.tools.WorkWithStoreableDataBase;

import java.io.IOException;
import java.util.List;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:18
 */
public class CmdCreate implements Cmd {
    private final String name = "create";
    private final int amArgs = 1;
    private StoreableDataBaseGlobalState storeableWorkState = null;
    private MultiFileDataBaseGlobalState multifileWorkState = null;

    public CmdCreate(StoreableDataBaseGlobalState dataBaseState) {
        storeableWorkState = dataBaseState;
    }

    public CmdCreate(MultiFileDataBaseGlobalState dataBaseState) {
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
            if (storeableWorkState.getTable(useTableName) != null) {
                System.err.println(useTableName + " exists");
            } else {
                List<Class<?>> types = WorkWithStoreableDataBase.createListOfTypes(args);
                storeableWorkState.createTable(useTableName, types);
                System.out.println("created");
            }
        } else {
            String useTableName = args.get(1);
            if (multifileWorkState.getTable(useTableName) != null) {
                System.err.println(useTableName + " exists");
            } else {
                multifileWorkState.createTable(useTableName);
                System.out.println("created");
            }
        }
    }
}
