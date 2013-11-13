package ru.fizteh.fivt.students.vlmazlov.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.GenericTableProviderFactory;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.DiffCountingTableProviderFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

public class StoreableTableProviderFactory implements TableProviderFactory  {
   	
    protected boolean autoCommit;
    
    //autoCommit disabled by default
    public StoreableTableProviderFactory() {
      autoCommit = false;
    }

    public StoreableTableProviderFactory(boolean autoCommit) {
      this.autoCommit = autoCommit;
    }

    public StoreableTableProvider create(String dir) throws IOException {
      if ((dir == null) || (dir.trim().isEmpty())) {
        throw new IllegalArgumentException("Directory not specified");
      }

      if (!(new File(dir)).exists()) {
        throw new IOException(dir + " doesn't exist");
      }

      try {
        return new StoreableTableProvider(dir, autoCommit);
      } catch (ValidityCheckFailedException ex) {
        throw new IllegalArgumentException(ex.getMessage());
      }
    }
}
