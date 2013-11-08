package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
public class StoreableTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private NewTableProviderFactory factory;
    private TableProvider provider;
    private Table table;
    private ArrayList<Class<?>> list;
    @Before
    public void create() throws IOException {
        list = new ArrayList<Class<?>>();
        list.add(int.class);
        list.add(boolean.class);
        factory = new NewTableProviderFactory();
        provider = factory.create(folder.newFolder().toString());
        table = provider.createTable("Table", list);
    }
    
    @Test(expected = IndexOutOfBoundsException.class) 
    public void negativeIndex() {
        Storeable st = new MyStoreable(table);
        st.getColumnAt(-1); 
    }
    @Test(expected = ColumnFormatException.class)
    public void incorrectTypeColumn() {
        Storeable st = new MyStoreable(table);
        st.setColumnAt(0, 1);
        st.setColumnAt(0, "key");
    }
    
    

}
