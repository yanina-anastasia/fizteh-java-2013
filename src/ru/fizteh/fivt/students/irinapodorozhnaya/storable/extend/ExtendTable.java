package ru.fizteh.fivt.students.irinapodorozhnaya.storable.extend;


import java.io.IOException;

import ru.fizteh.fivt.storage.structured.Table;

public interface ExtendTable extends Table {
    
    int getChangedValuesNumber();

    void loadAll() throws IOException;
}
