package ru.fizteh.fivt.students.dsalnikov.filemap;

import java.io.FileNotFoundException;
import ru.fizteh.fivt.students.dsalnikov.shell.Command;

public class PutCommand implements Command {

    public String getName() {
        return "put";
    }

    public int getArgsCount() {
        return 2;
    }

    public void execute(Object f, String[] args) throws FileNotFoundException {
        if (args.length != 3) {
            throw new IllegalArgumentException("Incorrect usage of command put:wrong amount" +
                    " of arguments");
        } else {
            FileMapState filemap = (FileMapState)f;
            String temp = filemap.setValue(args[1], args[2]);
            if (temp == null) {
                System.out.println("new pair " + args[1] + " " + args[2]);
                return;
            } else {
                System.out.println("overwrite " + temp);
                return;
            }
        }
    }
}