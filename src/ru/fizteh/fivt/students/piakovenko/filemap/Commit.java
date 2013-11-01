package ru.fizteh.fivt.students.piakovenko.filemap;

import ru.fizteh.fivt.students.piakovenko.shell.Commands;
import ru.fizteh.fivt.students.piakovenko.shell.MyException;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 27.10.13
 * Time: 10:21
 * To change this template use File | Settings | File Templates.
 */
public class Commit implements Commands {
    private final String name = "commit";
    private DataBase db = null;

    public Commit (DataBase t) {
        db = t;
    }

    public String getName() {
        return name;
    }

    public void changeCurrentStatus (Object obj) {
        db = (DataBase)obj;
    }

    public void perform(String[] args) throws MyException, IOException {
        if (args.length != 1) {
            throw new MyException(new Exception("Wrong number of arguments! Usage: commit"));
        }
        db.commit();
    }
}
