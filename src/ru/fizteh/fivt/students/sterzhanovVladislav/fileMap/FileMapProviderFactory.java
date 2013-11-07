package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class FileMapProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) {
        return new FileMapProvider(dir);
    }

}
