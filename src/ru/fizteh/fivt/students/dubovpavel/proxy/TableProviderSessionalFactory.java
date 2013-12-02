package ru.fizteh.fivt.students.dubovpavel.proxy;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageBuilder;
import ru.fizteh.fivt.students.dubovpavel.parallel.TableProviderStoreableParallelFactory;

import java.io.IOException;

public class TableProviderSessionalFactory extends TableProviderStoreableParallelFactory {
    public TableProvider create(String path) throws IOException {
        checkPath(path);
        StorageBuilder storageBuilder = new StorageBuilder();
        storageBuilder.setPath(false, path);
        TableSessionalBuilder dataBaseBuilder = new TableSessionalBuilder();
        storageBuilder.setDataBaseBuilder(dataBaseBuilder);
        return new TableProviderSessional<TableSessional>(storageBuilder.construct(), dataBaseBuilder);
    }
}
