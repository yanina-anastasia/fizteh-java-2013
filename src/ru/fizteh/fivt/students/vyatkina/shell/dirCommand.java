package ru.fizteh.fivt.students.vyatkina.shell;

import java.io.File;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: ava_katushka
 * Date: 25.09.13
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
public class dirCommand implements Command {
    @Override
     public  String getName () {
        return "dir";
    }
    @Override
    public void execute (Shell shell) {
        File [] currentDirectoryFiles = shell.currentDirectory.listFiles();
        Arrays.sort (currentDirectoryFiles, new Comparator<File>() {
            @Override
            public int compare (File o1, File o2) {
                if (o1.isDirectory () && o2.isDirectory()) {
                    return o1.toString ().compareTo (o2.toString ());
                } else if (o1.isDirectory ()) {
                    return -1;
                } else if (o2.isDirectory ()) {
                    return 1;
                } else {
                    return  o1.toString ().compareTo (o2.toString ());
                }
            }
        });
        for (File f: currentDirectoryFiles) {
            System.out.println (f.getName());
        }

    }
}
