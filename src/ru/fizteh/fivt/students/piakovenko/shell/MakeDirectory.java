package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 21:19
 * To change this template use File | Settings | File Templates.
 */
public class MakeDirectory implements Commands {
    private final String name = "mkdir";
    private CurrentStatus currentStatus;

    public MakeDirectory(CurrentStatus cs) {
        currentStatus = cs;
    }

    public void changeCurrentStatus (Object obj){
        currentStatus = (CurrentStatus)obj;
    }

    public String getName() {
        return name;
    }


    public void perform(String[] array) throws MyException, IOException {
        if (array.length != 2) {
            throw new MyException(new Exception("Wrong arguments! Usage ~ mkdir <name of new directory>"));
        }
        File f = new File(array[1]);
        if (!f.isAbsolute()) {
            f = new File(currentStatus.getCurrentDirectory(), array[1]);
        }
        if (!f.mkdirs()){
            throw new MyException(new Exception("Unable to create this directory - " + f.getCanonicalPath()));
        }
    }
}
