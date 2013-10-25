package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.students.adanilyak.modernfilemap.FileMapState;

import java.util.Vector;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 0:27
 */
public class CmdFmExit implements Cmd {
    private final String name = "exit";
    private final int amArgs = 0;
    private FileMapState dataFileMap;

    public CmdFmExit(FileMapState stateTable) {
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
        dataFileMap.commit();
        System.exit(0);
    }
}
