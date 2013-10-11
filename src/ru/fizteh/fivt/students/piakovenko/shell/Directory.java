package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
 */
public class Directory implements Commands {
    private final String name = "dir";
    private CurrentStatus currentStatus;

    public Directory(CurrentStatus cs) {
        currentStatus = cs;
    }

    public String getName() {
        return name;
    }


    public void perform(String args) throws MyException, IOException {
        if (!args.isEmpty()) {
            throw new MyException(new Exception("Wrong arguments! Usage ~ dir"));
        }
        if (!currentStatus.getCurrentFile().isDirectory()) {
            throw new MyException(new Exception("Error! " + currentStatus.getCurrentDirectory() + " is not a directory!"));
        }
        for (String s: currentStatus.getCurrentFile().list()) {
            System.out.println(s);
        }
    }
}
