package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.storage.strings.Table;

import java.util.Vector;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 0:00
 */
public class CmdFmPut implements Cmd {
    private final String name = "put";
    private final int amArgs = 2;
    private Table dataFileMap;

    public CmdFmPut(Table stateTable) {
        dataFileMap = stateTable;
    }

    public void changeState(Table stateTable) {
        dataFileMap = stateTable;
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
    public void work(Vector<String> args) throws Exception {
        String key = args.get(1);
        String value = args.get(2);
        String result = dataFileMap.put(key, value);
        if (result == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(result);
        }
    }
}
