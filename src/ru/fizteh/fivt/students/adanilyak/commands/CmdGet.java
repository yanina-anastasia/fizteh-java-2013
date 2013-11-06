package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.adanilyak.filemap.FileMapGlobalState;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableDataBaseGlobalState;
import ru.fizteh.fivt.students.adanilyak.tools.StoreableCmdParseAndExecute;

import java.io.IOException;
import java.util.List;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:18
 */
public class CmdGet implements Cmd {
    private final String name = "get";
    private final int amArgs = 1;
    private StoreableDataBaseGlobalState storeableWorkState = null;
    private FileMapGlobalState multifileWorkState = null;

    public CmdGet(StoreableDataBaseGlobalState dataBaseState) {
        storeableWorkState = dataBaseState;
    }

    public CmdGet(FileMapGlobalState dataBaseState) {
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
                Storeable result = storeableWorkState.currentTable.get(key);
                if (result == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("found");
                    System.out.println(StoreableCmdParseAndExecute.outPutToUser(result, storeableWorkState.currentTable, storeableWorkState.currentTableManager));
                }
            } else {
                System.out.println("no table");
            }
        } else {
            if (multifileWorkState.currentTable != null) {
                String key = args.get(1);
                String result = multifileWorkState.get(key);
                if (result == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("found");
                    System.out.println(result);
                }
            } else {
                System.out.println("no table");
            }
        }
    }
}
