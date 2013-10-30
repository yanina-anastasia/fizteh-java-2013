package ru.fizteh.fivt.students.vyatkina.database.tables;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.vyatkina.database.providers.MultiTableProvider;

import java.io.IOException;
import java.util.Map;

public class MultiTable extends AbstractTable {

    MultiTableProvider tableProvider;
    protected String name;

    public MultiTable (String name, Map<String, String> values, MultiTableProvider tableProvider) {
        this.name = name;
        this.values = values;
        this.tableProvider = tableProvider;
    }

    @Override
    public String getName () {
        return name;
    }

    @Override
    public int commit () throws IllegalArgumentException {
        try {
            tableProvider.writeDatabaseOnDisk ();
        }
        catch (IOException e) {
            throw new IllegalArgumentException (e.getMessage ());
        }
        return 0;
    }

}
