package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 21:32
 * To change this template use File | Settings | File Templates.
 */
public class Remove implements Commands {
    private final String name = "rm";
    private CurrentStatus currentStatus;

    public static void removeRecursively (File f) throws MyException, IOException{
        if (!f.isDirectory()) {
            if (!f.delete()) {
                throw new MyException(new Exception("Error! Unable to delete file - " + f.getCanonicalPath()));
            }
        } else {
            for (File file: f.listFiles()) {
                removeRecursively(file);
            }
            if (!f.delete()) {
                throw new MyException(new Exception("Error! Unable to delete file - " + f.getCanonicalPath()));
            }
        }
    }

    public Remove(CurrentStatus cs) {
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
            throw new MyException(new Exception("Wrong arguments! Usage ~ rm <removeDirectory>"));
        }
        File f;
        f = new File(array[1]);
        if (!f.isAbsolute()) {
            f = new File(currentStatus.getCurrentDirectory(), array[1]);
        }
        if (f.getCanonicalPath().equals(currentStatus.getCurrentDirectory())) {
            throw new MyException(new Exception("Trying to delete current directory!"));
        }
        if (!f.exists()) {
            throw new MyException(new Exception(f.getCanonicalPath() + " doesn't exist!"));
        }
        removeRecursively(f);
    }
}
