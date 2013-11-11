package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.text.ParseException;
import ru.fizteh.fivt.students.vlmazlov.filemap.StringTable;
import ru.fizteh.fivt.students.vlmazlov.filemap.GenericTable;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.DiffCountingTableProvider;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityCheckFailedException;

public class StringTableProvider extends GenericTableProvider<String, StringTable> implements DiffCountingTableProvider {

	public StringTableProvider(String root, boolean autoCommit) throws ValidityCheckFailedException {
		super(root, autoCommit);
	}

	@Override
	protected StringTable instantiateTable(String name, Object args[]) {
		return new StringTable(name, autoCommit);
	}

	public StringTable createTable(String name) {
		return super.createTable(name, null);
	}

	@Override
	public String deserialize(StringTable table, String value) {
		return new String(value);
	} 
    
    @Override
    public String serialize(StringTable table, String value) {
    	return new String(value);
    }

  	@Override
  	public void read() throws IOException, ValidityCheckFailedException {
  		for (File file : ProviderReader.getTableDirList(this)) {

  			ValidityChecker.checkMultiTableRoot(file);

  			StringTable table = createTable(file.getName());
  			ProviderReader.readMultiTable(file, table, this);
  			//read data has to be preserved
            table.commit();
  		}
  	}

    @Override
    public void write() throws IOException, ValidityCheckFailedException {
      for (Map.Entry<StringTable, File> entry : ProviderWriter.getTableDirMap(this).entrySet()) {

        ValidityChecker.checkMultiTableRoot(entry.getValue());

        ProviderWriter.writeMultiTable(entry.getKey(), entry.getValue(), this);
      }
    }
  }