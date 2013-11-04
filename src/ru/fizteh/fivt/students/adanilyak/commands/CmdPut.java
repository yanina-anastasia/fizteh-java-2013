package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.adanilyak.modernfilemap.FileMapGlobalState;
import ru.fizteh.fivt.students.adanilyak.multifilehashmap.MultiFileDataBaseGlobalState;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableDataBaseGlobalState;
import ru.fizteh.fivt.students.adanilyak.tools.StoreableCmdParseAndExecute;

import java.io.IOException;
import java.util.List;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:17
 */
public class CmdPut implements Cmd {
    private final String name = "put";
    private final int amArgs = 2;
    private StoreableDataBaseGlobalState storeableWorkState = null;
    private FileMapGlobalState multifileWorkState = null;

    public CmdPut(StoreableDataBaseGlobalState dataBaseState) {
        storeableWorkState = dataBaseState;
    }

    public CmdPut(FileMapGlobalState dataBaseState) {
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
                String key = args.get(1);
                String value = args.get(2);
                Storeable toPut = StoreableCmdParseAndExecute.putStringIntoStoreable(value, storeableWorkState.currentTable, storeableWorkState.currentTableManager);
                Storeable result = storeableWorkState.currentTable.put(key, toPut);
                if (result == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite");
                    System.out.println(StoreableCmdParseAndExecute.outPutToUser(result, storeableWorkState.currentTable, storeableWorkState.currentTableManager));
                }
            } else {
                System.out.println("no table");
            }
        } else {
            if (multifileWorkState.currentTable != null) {
                String key = args.get(1);
                String value = args.get(2);
                String result = multifileWorkState.put(key, value);
                if (result == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite");
                    System.out.println(result);
                }
            } else {
                System.out.println("no table");
            }
        }
    }
}
