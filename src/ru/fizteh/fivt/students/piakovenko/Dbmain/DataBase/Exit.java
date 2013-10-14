package ru.fizteh.fivt.students.piakovenko.Dbmain.DataBase;

import ru.fizteh.fivt.students.piakovenko.Dbmain.Commands;
import ru.fizteh.fivt.students.piakovenko.Dbmain.MyException;

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

    public String getName() {
        return name;
    }


    public void perform(String[] s) throws MyException, IOException {
        if (s.length != 1) {
            throw new MyException(new Exception("Wrong arguments! Usage ~ exit"));
        }
        try {
            DataBase.saveDataBase();
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
