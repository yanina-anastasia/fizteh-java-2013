package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.students.adanilyak.multifilehashmap.MultiFileDataBaseGlobalState;

import java.io.IOException;
import java.util.List;

/**
 * User: Alexander
 * Date: 28.10.13
 * Time: 16:14
 */
public class CmdRollback implements Cmd {
    private final String name = "rollback";
    private final int amArgs = 0;
    private MultiFileDataBaseGlobalState workState = null;

    public CmdRollback(MultiFileDataBaseGlobalState dataBaseState) {
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
        if (workState.getCurrentTable() != null) {
            System.out.println(workState.rollback());
        } else {
            System.out.println("no table");
        }
    }
}
