package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import  java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        try {
            Shell shell = new Shell();
            if (args.length == 0) {
                shell.interactiveVersion();
            } else {
                String arguments = StringMethods.join(Arrays.asList(args), " ");
                shell.batchVersion(arguments);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
