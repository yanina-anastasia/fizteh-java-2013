package ru.fizteh.fivt.students.piakovenko.multifilehashmap;

import ru.fizteh.fivt.students.piakovenko.shell.Commands;
import ru.fizteh.fivt.students.piakovenko.shell.MyException;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 21.10.13
 * Time: 0:11
 * To change this template use File | Settings | File Templates.
 */
public class Create implements Commands {
    private final String name = "create";
    private DataBasesCommander dbc = null;

    public Create (DataBasesCommander t) {
        dbc = t;
    }

    public String getName() {
        return name;
    }

    public void changeCurrentStatus (Object obj) {
        dbc = (DataBasesCommander)obj;
    }

    public void perform(String[] args) throws MyException, IOException {
        if (args.length != 2) {
            throw new MyException(new Exception("Wrong number of arguments! Usage: create <keyValue>"));
        }
        dbc.create(args[1]);
    }

}
