package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableDataBaseGlobalState;

import java.io.IOException;
import java.util.List;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:18
 */
public class CmdRemove implements Cmd {
    private final String name = "remove";
    private final int amArgs = 1;
    private StoreableDataBaseGlobalState workState;

    public CmdRemove(StoreableDataBaseGlobalState dataBaseState) {
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
        if (workState.currentTable != null) {
            String key = args.get(1);
            Storeable result = workState.currentTable.remove(key);
            if (result == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        } else {
            System.out.println("no table");
        }
    }
}
