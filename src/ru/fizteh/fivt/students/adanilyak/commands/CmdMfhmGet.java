package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.students.adanilyak.multifilehashmap.mfhmDataBaseGlobalState;

import java.util.Vector;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:18
 */
public class CmdMfhmGet implements Cmd {
    private final String name = "get";
    private final int amArgs = 1;
    private mfhmDataBaseGlobalState workState;

    public CmdMfhmGet(mfhmDataBaseGlobalState dataBaseState) {
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
            String key = args.get(1);
            String result = workState.get(key);
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
