package ru.fizteh.fivt.students.kislenko.multifilemap;

public class TableProviderFactory implements ru.fizteh.fivt.storage.strings.TableProviderFactory {
    @Override
    public TableProvider create(String dir) {
        TableProvider provider = new TableProvider();
        provider.createTable(dir);
        return provider;
    }
}