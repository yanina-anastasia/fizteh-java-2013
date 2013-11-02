package ru.fizteh.fivt.students.piakovenko.filemap;


import ru.fizteh.fivt.students.piakovenko.shell.Commands;
import ru.fizteh.fivt.students.piakovenko.shell.MyException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 12.10.13
 * Time: 22:44
 * To change this template use File | Settings | File Templates.
 */
public class Get implements Commands {
    private final String name = "get";
    private DataBase db;

    public Get (DataBase dataBase) {
        db = dataBase;
    }

    public void changeCurrentStatus (Object obj){
        db = (DataBase)obj;
    }

    public String getName() {
        return name;
    }

    public void perform(String[] args) throws MyException {
        if (db == null) {
            System.out.println("no table");
            return;
        }
        if (args.length != 2) {
            throw new MyException(new Exception("Wrong number of arguments! Usage: get <keyValue>"));
        }
        db.get(args[1]);
    }
}
