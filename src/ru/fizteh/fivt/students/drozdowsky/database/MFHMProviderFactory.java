package ru.fizteh.fivt.students.drozdowsky.database;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class MFHMProviderFactory implements TableProviderFactory {
    public MultiFileHashMap create(String dir) {
        return new MultiFileHashMap(dir);
    }
}
