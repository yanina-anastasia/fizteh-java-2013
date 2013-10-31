package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class MyTableProviderFactory implements TableProviderFactory {
    private File curDir;
    
    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Bad directory");
        }
        if (dir.contains(".") || dir.contains("/") || dir.contains("\\")) {
            throw new IllegalArgumentException("Bad directory");
        }
        curDir = new File(dir);
        if (!curDir.exists()) {
            throw new IllegalArgumentException("Directory doesn't exists");
        }
        if (!curDir.isDirectory()) {
            throw new IllegalArgumentException("Argument is not a directory");
        }
        return new TableProviderCommands(curDir);
    }
}
