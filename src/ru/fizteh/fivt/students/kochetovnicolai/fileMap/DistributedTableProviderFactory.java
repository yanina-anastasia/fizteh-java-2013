package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class DistributedTableProviderFactory implements TableProviderFactory {
    HashMap<String, DistributedTableProvider> providers;

    public DistributedTableProviderFactory() {
        providers = new HashMap<>();
    }

    @Override
    public DistributedTableProvider create(String dir) throws IOException, IllegalArgumentException {
        if (dir == null || dir.equals("")) {
            throw new IllegalArgumentException("directory couldn't be null");
        }
        File path = new File(dir);
        try {
            path = path.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid directory", e);
        }
        String directory = path.getAbsolutePath();
        if (!providers.containsKey(directory)) {
            providers.put(directory, new DistributedTableProvider(path));
        }
        return providers.get(directory);
    }
}
