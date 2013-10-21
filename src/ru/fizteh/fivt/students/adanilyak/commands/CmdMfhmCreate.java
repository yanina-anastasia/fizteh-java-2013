package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.students.adanilyak.multifilehashmap.mfhmDataBaseGlobalState;

import java.util.Vector;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:18
 */
public class CmdMfhmCreate implements Cmd {
    private final String name = "create";
    private final int amArgs = 1;
    private mfhmDataBaseGlobalState workState;

    public CmdMfhmCreate(mfhmDataBaseGlobalState dataBaseState) {
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
    public void work(Vector<String> args) throws Exception {
        String useTableName = args.get(1);
        if (workState.getTable(useTableName) != null) {
            System.err.println(useTableName + " exists");
        } else {
            workState.createTable(useTableName);
            System.out.println("created");
        }
    }
}
