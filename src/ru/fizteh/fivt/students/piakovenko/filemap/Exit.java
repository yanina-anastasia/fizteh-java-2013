package ru.fizteh.fivt.students.piakovenko.filemap;

import ru.fizteh.fivt.students.piakovenko.shell.Commands;
import ru.fizteh.fivt.students.piakovenko.shell.CurrentStatus;
import ru.fizteh.fivt.students.piakovenko.shell.MyException;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 11.10.13
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
public class Exit implements Commands {
    private final String name = "exit";
    private DataBase db;

    public String getName() {
        return name;
    }

    public Exit (DataBase dataBase) {
        db = dataBase;
    }

    public void changeCurrentStatus (Object obj){
        db = (DataBase)obj;
    }


    public void perform(String[] s) throws MyException, IOException {
        if (s.length != 1) {
            throw new MyException(new Exception("Wrong arguments! Usage ~ exit"));
        }
        if (db == null) {
            return;
        }
        try {
            db.saveDataBase();
        } catch (MyException e) {
            System.err.println("Error! " + e.what());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error! " + e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }
}
