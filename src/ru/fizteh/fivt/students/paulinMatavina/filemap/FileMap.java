package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.io.File;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class FileMap {
    public static void main(String[] args) {
        DbState state = new DbState(System.getProperty("fizteh.db.dir") 
                            + File.separator + "db.dat", 0, 0);        
        CommandRunner.run(args, state);
    }
}
