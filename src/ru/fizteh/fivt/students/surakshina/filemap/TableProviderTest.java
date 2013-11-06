package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.IOException;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.storage.strings.Table;

public class TableProviderTest {
    private TableProviderFactory factory;
    private TableProvider provider;
   
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Before
    public void begin() throws IOException {
        factory = new NewTableProviderFactory();
        provider = factory.create(folder.newFolder().toString());
    }
    
    @Test
    public void getTest() {
        String tableName = "table";
        Table table = provider.createTable("table");
        Assert.assertNotNull(table);
        Assert.assertEquals(tableName, table.getName());
        provider.removeTable(tableName);
        Assert.assertNull(provider.getTable(tableName));
    }
}
