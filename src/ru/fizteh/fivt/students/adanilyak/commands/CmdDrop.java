package ru.fizteh.fivt.students.adanilyak.commands;

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
    private StoreableDataBaseGlobalState workState;

    public CmdDrop(StoreableDataBaseGlobalState dataBaseState) {
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
        if (!CheckOnCorrect.goodArg(useTableName)) {
            throw new IllegalArgumentException("Bad table name");
        }
        if (workState.getTable(useTableName) == null) {
            System.err.println(useTableName + " not exists");
            throw new IllegalStateException(useTableName + " not exists");
        } else {
            workState.removeTable(useTableName);
            System.out.println("dropped");
        }
    }
}
