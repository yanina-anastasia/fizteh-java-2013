package ru.fizteh.fivt.students.piakovenko.filemap;

import ru.fizteh.fivt.students.piakovenko.shell.Commands;
import ru.fizteh.fivt.students.piakovenko.shell.MyException;


public class Put implements Commands {
    private final String name = "put";
    private DataBase db;

    public Put (DataBase dataBase) {
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
        if (args.length != 3) {
            throw new MyException(new Exception("Wrong number of arguments! Usage: get <keyValue>"));
        }
        db.put(args[1], args[2]);
    }
}
