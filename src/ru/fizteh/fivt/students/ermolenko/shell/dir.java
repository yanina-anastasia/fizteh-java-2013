package ru.fizteh.fivt.students.ermolenko.shell;


import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class dir implements Command {

    public String getName() {
        return "dir";
    }

    public void executeCmd(Shell shell, String[] args) throws IOException {
        if (0 == args.length) {
            File currentDirectory = new File(shell.getState().getPath().toString());
            File[] listOfFiles = currentDirectory.listFiles();
            PrintStream print = new PrintStream(System.out);
            for (int i = 0; i < listOfFiles.length; ++i) {
                print.println(listOfFiles[i].getName());
            }
        } else {
            throw new IOException("not allowed number of arguments");
        }
    }
}
