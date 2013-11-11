package ru.fizteh.fivt.students.vlmazlov.multifilemap;


import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import java.io.FileNotFoundException;

public class FileMapProviderFactory implements DiffCountingTableProviderFactory  {
    boolean autoCommit;

    //autoCommit disabled by default
   	public FileMapProviderFactory() {
      autoCommit = false;
    }

    public FileMapProviderFactory(boolean autoCommit) {
      this.autoCommit = autoCommit;
    }

    public FileMapProvider create(String dir) {
    	try {
        ValidityChecker.checkMultiTableRoot(dir);
      } catch (ValidityCheckFailedException ex) {
        throw new IllegalArgumentException(ex.getMessage());
      }

    	try {
    		return new FileMapProvider(dir, autoCommit);
   	  } catch (ValidityCheckFailedException ex) {
   	  	throw new IllegalArgumentException("Validity check failed: " + ex.getMessage());
      }
    }
}
