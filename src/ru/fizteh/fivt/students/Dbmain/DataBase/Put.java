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
public class Put implements Commands {
    private final String name = "put";
    public String getName() {
        return name;
    }
    public void perform(String[] args) throws MyException {
        if (args.length != 3) {
            throw new MyException(new Exception("Wrong number of argumnets! Usage: get <keyValue>"));
        }
        DataBase.put(args[1], args[2]);
    }
}
