package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.students.adanilyak.multifilehashmap.DataBaseGlobalState;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableDataBaseGlobalState;
import ru.fizteh.fivt.students.adanilyak.tools.WorkWithStoreableDataBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:18
 */
public class CmdCreate implements Cmd {
    private final String name = "create";
    private final int amArgs = 1;
    private StoreableDataBaseGlobalState workState;

    public CmdCreate(StoreableDataBaseGlobalState dataBaseState) {
        workState = dataBaseState;
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
        String useTableName = args.get(1);
        if (workState.getTable(useTableName) != null) {
            System.err.println(useTableName + " exists");
        } else {
            List<Class<?>> types;
            if (args.size() == 2) {
                types = new ArrayList<>();
                types.add(String.class);
            } else {
                types = WorkWithStoreableDataBase.createListOfTypes(args);
            }
            workState.createTable(useTableName, types);
            System.out.println("created");
        }
    }
}
