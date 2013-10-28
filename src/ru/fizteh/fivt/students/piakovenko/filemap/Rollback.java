package ru.fizteh.fivt.students.piakovenko.filemap;

import ru.fizteh.fivt.students.piakovenko.shell.Commands;
import ru.fizteh.fivt.students.piakovenko.shell.MyException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 27.10.13
 * Time: 10:26
 * To change this template use File | Settings | File Templates.
 */
public class Rollback implements Commands {
    private final String name = "rollback";
    private DataBase db;

    public Rollback (DataBase dataBase) {
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
        if (args.length != 1) {
            throw new MyException(new Exception("Wrong number of arguments! Usage: rollback"));
        }
        db.rollback();
    }
}
