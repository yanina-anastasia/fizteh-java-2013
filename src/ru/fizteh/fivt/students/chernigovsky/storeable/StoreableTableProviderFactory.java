package ru.fizteh.fivt.students.chernigovsky.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoreableTableProviderFactory implements TableProviderFactory {
    @Override
    public StoreableTableProvider create(String dir) throws IOException {
        if (dir == null) {
            throw new IllegalArgumentException("dir is null");
        }
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(dir);
        if (dir.isEmpty() || matcher.find()) {
            throw new IllegalArgumentException("dir is wrong");
        }

        File dbDirectory = new File(dir);
        if (!dbDirectory.exists() || !dbDirectory.isDirectory()) {
            throw new IOException("no such directory");
        }

        return new StoreableTableProvider(dbDirectory, false);
    }
}