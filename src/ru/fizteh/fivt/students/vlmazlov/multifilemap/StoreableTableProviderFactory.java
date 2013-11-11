package ru.fizteh.fivt.students.vlmazlov.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.DiffCountingTableProviderFactory;
import java.io.FileNotFoundException;

public class StoreableTableProviderFactory 
extends GenericTableProviderFactory<Storeable, StoreableTable, StoreableTableProvider> implements TableProviderFactory  {
   	
    public StringTableProviderFactory() {
      super();
    }

    public StringTableProviderFactory(boolean autoCommit) {
      super(autoCommit);
    }

    protected StoreableTableProvider instantiateTableProvider(String dir) throws ValidityCheckFailedException {
    	return new StoreableTableProvider(dir, autoCommit);
    }
}
