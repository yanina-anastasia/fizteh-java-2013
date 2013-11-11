package ru.fizteh.fivt.students.vlmazlov.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.GenericTableProviderFactory;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.DiffCountingTableProviderFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

public class StoreableTableProviderFactory 
extends GenericTableProviderFactory<Storeable, StoreableTable, StoreableTableProvider> implements TableProviderFactory  {
   	
    public StoreableTableProviderFactory() {
      super();
    }

    public StoreableTableProviderFactory(boolean autoCommit) {
      super(autoCommit);
    }

    protected StoreableTableProvider instantiateTableProvider(String dir) throws ValidityCheckFailedException {
    	return new StoreableTableProvider(dir, autoCommit);
    }
}
