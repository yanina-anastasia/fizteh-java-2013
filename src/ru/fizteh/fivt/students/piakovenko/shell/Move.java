package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 22:48
 * To change this template use File | Settings | File Templates.
 */
public class Move implements Commands {
    private final String name = "mv";
    private CurrentStatus currentStatus;

    public Move(CurrentStatus cs) {
        currentStatus = cs;
    }

    public String getName() {
        return name;
    }


    public void perform(String args) throws MyException, IOException {
        String[] array = args.trim().split("\\s+");
        if (array.length != 2) {
            throw new MyException(new Exception("Wrong arguments! Usage mv <source> <destination>"));
        }
        Copy c = new Copy(currentStatus);
        Remove r = new Remove(currentStatus);
        c.perform(array[0]+ ' '+ array[1]);
        r.perform(array[0]);
    }
}
