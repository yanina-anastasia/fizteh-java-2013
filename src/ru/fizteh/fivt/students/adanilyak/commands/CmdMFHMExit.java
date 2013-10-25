package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.students.adanilyak.multifilehashmap.DataBaseGlobalState;

import java.util.Vector;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:18
 */
public class CmdMFHMExit implements Cmd {
    private final String name = "exit";
    private final int amArgs = 0;
    private DataBaseGlobalState workState;

    public CmdMFHMExit(DataBaseGlobalState dataBaseState) {
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
        if (workState.getCurrentTable() != null) {
            workState.commit();
        }
        System.exit(0);
    }
}
