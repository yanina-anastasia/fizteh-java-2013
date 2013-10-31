package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class MyTableProviderFactory implements TableProviderFactory {
    public TableProvider myTableProvider;
    private File curDir;
    
    @Override
    public TableProvider create(String dir) {
        String getPropertyString = dir;
        if (getPropertyString == null) {
            throw new IllegalArgumentException("Bad property");
        }
        curDir = new File(getPropertyString);
        if (!curDir.exists()) {
            throw new IllegalArgumentException("Directory doesn't exists");
        }
        if (!curDir.isDirectory()) {
            throw new IllegalArgumentException("Argument is not a directory");
        }
        myTableProvider = new TableProviderCommands(curDir);
        return myTableProvider;
    }
}
