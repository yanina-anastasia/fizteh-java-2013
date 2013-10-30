package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 17.10.13
 * Time: 17:56
 * To change this template use File | Settings | File Templates.
 */
public class Start {
    public static void main(String[] args) {
        Shell shell = new Shell();
        shell.initializeBasicCommands();
        shell.start(args);
    }
}
