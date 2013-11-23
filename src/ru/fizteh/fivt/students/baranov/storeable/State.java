package ru.fizteh.fivt.students.baranov.storeable;

import java.io.File;

public class State {
    public String currentDir;
    public MyTableProvider tableProvider;
    public MyTable table;

    public State() {
        File file = new File("");
        currentDir = file.getAbsolutePath();
    }

    public void printCurrentDir() {
        System.out.println(currentDir);
    }
    
    public String getProperty(State myState) {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            MyTableBuilder tableBuilder = new MyTableBuilder(myState.table.provider, myState.table);
            if (!table.save(tableBuilder)) {
                System.err.println("File was not saved");
            }
            System.err.println("Error with getting property");
            System.exit(1);
        }
        return path;
    }
}
