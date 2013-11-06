package ru.fizteh.fivt.students.surakshina.filemap;

import static org.junit.Assert.*;

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
        assertNotNull(table);
        assertEquals(tableName, table.getName());
        provider.removeTable(tableName);
        assertNull(provider.getTable(tableName));
    }
    @Test
    public void getTestTable() {
        Table table = provider.createTable("Table");
        assertNotNull(provider.getTable("Table"));
        assertNull(provider.getTable("NotExistTable"));
        assertEquals(table, provider.getTable("Table"));
        provider.removeTable("Table");
    }
    
    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistsTable() {
        provider.removeTable("NotExistsTable");
    }
    @Test(expected = RuntimeException.class)
    public void testIncorrectName() {
        provider.getTable("//");
    }
}
