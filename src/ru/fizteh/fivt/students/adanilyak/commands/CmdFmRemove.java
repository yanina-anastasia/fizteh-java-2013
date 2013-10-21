package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.storage.strings.Table;

import java.util.Vector;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 11:17
 */
public class CmdFmRemove implements Cmd {
    private final String name = "remove";
    private final int amArgs = 1;
    private Table dataFileMap;

    public CmdFmRemove(Table stateTable) {
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
        String result = dataFileMap.remove(key);
        if (result == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
