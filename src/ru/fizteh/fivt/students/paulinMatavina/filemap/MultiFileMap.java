package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.util.zip.DataFormatException;

import ru.fizteh.fivt.students.paulinMatavina.utils.CommandRunner;

public class MultiFileMap {
    public static void main(String[] args) {
        try {
            MultiDbState state = new MultiDbState();  
            CommandRunner.run(args, state);
        } catch (DataFormatException e) {
            System.err.println("multifilemap: " + e.getMessage());
            System.exit(1);
        }
    }
}
