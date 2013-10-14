package ru.fizteh.fivt.students.piakovenko.Dbmain.DataBase;

import ru.fizteh.fivt.students.piakovenko.Dbmain.Commands;
import ru.fizteh.fivt.students.piakovenko.Dbmain.MyException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 12.10.13
 * Time: 22:44
 * To change this template use File | Settings | File Templates.
 */
public class Get implements Commands {
    private final String name = "get";

    public String getName() {
        return name;
    }

    public void perform(String[] args) throws MyException {
        if (args.length != 2) {
            throw new MyException(new Exception("Wrong number of argumnets! Usage: get <keyValue>"));
        }
        DataBase.get(args[1]);
    }
}
