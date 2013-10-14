package ru.fizteh.fivt.students.piakovenko.Dbmain.DataBase;

import ru.fizteh.fivt.students.piakovenko.Dbmain.Commands;
import ru.fizteh.fivt.students.piakovenko.Dbmain.MyException;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 12.10.13
 * Time: 22:45
 * To change this template use File | Settings | File Templates.
 */
public class Remove implements Commands {
    private final String name = "remove";
    public String getName() {
        return name;
    }
    public void perform(String[] args) throws IOException, MyException{
        if (args.length != 2) {
            throw new MyException(new Exception("Wrong number of argumnets! Usage: get <keyValue>"));
        }
        DataBase.remove(args[1]);
    }
}
