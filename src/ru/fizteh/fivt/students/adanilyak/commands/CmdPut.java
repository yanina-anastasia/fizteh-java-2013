package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableDataBaseGlobalState;
import ru.fizteh.fivt.students.adanilyak.tools.StoreableCmdParseAndExecute;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:17
 */
public class CmdPut implements Cmd {
    private final String name = "put";
    private final int amArgs = 2;
    private StoreableDataBaseGlobalState workState;

    public CmdPut(StoreableDataBaseGlobalState dataBaseState) {
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
            String value = args.get(2);
            Storeable toPut = StoreableCmdParseAndExecute.putStringIntoStoreable(value, workState.currentTable, workState.currentTableManager);
            Storeable result = workState.currentTable.put(key, toPut);
            if (result == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(StoreableCmdParseAndExecute.outPutToUser(result, workState.currentTable, workState.currentTableManager));
            }
        } else {
            System.out.println("no table");
        }
    }
}
