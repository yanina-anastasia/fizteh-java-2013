package ru.fizteh.fivt.students.vyatkina.database.tables;

import ru.fizteh.fivt.students.vyatkina.database.providers.SingleTableProvider;

import java.io.IOException;
import java.util.Map;

public class SingleTable extends AbstractTable {

    public SingleTableProvider tableProvider;

    public SingleTable (Map<String, String> values, SingleTableProvider tableProvider) {
        this.values = values;
        this.tableProvider = tableProvider;
    }

    @Override
    public String getName () {
        throw new UnsupportedOperationException ("Get name operation is not supported");
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
