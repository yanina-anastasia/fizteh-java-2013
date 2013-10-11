package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 20:29
 * To change this template use File | Settings | File Templates.
 */
public class ChangeDirectory implements Commands {
    private final String name = "cd";
    private CurrentStatus currentStatus;

    public ChangeDirectory(CurrentStatus cs) {
        currentStatus = cs;
    }

    public String getName() {
        return name;
    }


    public void perform(String args) throws MyException, IOException {
        String[] array = args.trim().split("\\s+");
        if (array.length != 1) {
            throw new MyException(new Exception("Wrong arguments! Usage ~ cd <destination>"));
        }
        File f =  new File(currentStatus.getCurrentDirectory(), array[0]);
        currentStatus.changeCurrentDirectory(f.getCanonicalPath());
    }

}
