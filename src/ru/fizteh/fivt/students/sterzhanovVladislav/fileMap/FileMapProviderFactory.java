package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class FileMapProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) {
        return new FileMapProvider(dir);
    }

}
