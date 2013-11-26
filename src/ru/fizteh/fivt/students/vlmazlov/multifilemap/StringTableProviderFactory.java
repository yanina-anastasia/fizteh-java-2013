package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.vlmazlov.filemap.StringTable;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.DiffCountingTableProviderFactory;
import java.io.FileNotFoundException;
import java.io.File;

public class StringTableProviderFactory implements DiffCountingTableProviderFactory  {
    protected boolean autoCommit;

    //autoCommit disabled by default
    public StringTableProviderFactory() {
      autoCommit = false;
    }

    public StringTableProviderFactory(boolean autoCommit) {
      this.autoCommit = autoCommit;
    }

    public StringTableProvider create(String dir) {

      if (!(new File(dir)).exists()) {
        (new File(dir)).mkdir();
      }

      try {
        return new StringTableProvider(dir, autoCommit);
      } catch (ValidityCheckFailedException ex) {
        throw new IllegalArgumentException(ex.getMessage());
      }
    }  	
}
