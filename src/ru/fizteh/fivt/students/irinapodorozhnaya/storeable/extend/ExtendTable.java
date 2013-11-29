package ru.fizteh.fivt.students.irinapodorozhnaya.storeable.extend;


import java.io.IOException;

import ru.fizteh.fivt.storage.structured.Table;

public interface ExtendTable extends Table {
    
    int getChangedValuesNumber();

    void loadAll() throws IOException;
}
