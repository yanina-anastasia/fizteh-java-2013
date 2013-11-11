package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.vlmazlov.filemap.StringTable;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.DiffCountingTableProviderFactory;
import java.io.FileNotFoundException;

public class StringTableProviderFactory 
extends GenericTableProviderFactory<String, StringTable, StringTableProvider> implements DiffCountingTableProviderFactory  {
   	
    public StringTableProviderFactory() {
      super();
    }

    public StringTableProviderFactory(boolean autoCommit) {
      super(autoCommit);
    }

    protected StringTableProvider instantiateTableProvider(String dir) throws ValidityCheckFailedException {
    	return new StringTableProvider(dir, autoCommit);
    }
}
