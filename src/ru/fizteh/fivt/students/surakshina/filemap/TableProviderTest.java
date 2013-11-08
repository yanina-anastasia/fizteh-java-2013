package ru.fizteh.fivt.students.surakshina.filemap;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class TableProviderTest {
    private TableProviderFactory factory;
    private TableProvider provider;
    private ArrayList<Class<?>> list;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void begin() throws IOException {
        factory = new NewTableProviderFactory();
        provider = factory.create(folder.newFolder().toString());
        list = new ArrayList<Class<?>>();
        list.add(int.class);
        list.add(double.class);
        list.add(float.class);
        list.add(String.class);
        list.add(boolean.class);
        list.add(byte.class);
        list.add(long.class);

    }

    @Test(expected = IllegalArgumentException.class)
    public void checkTableName() {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkColumnName() throws IOException {
        ArrayList<Class<?>> listNew = new ArrayList<Class<?>>();
        listNew.add(Vector.class);
        provider.createTable("table1", listNew);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkColumnNameNull() throws IOException {
        provider.createTable("table", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkColumnNameEmpty() throws IOException {
        ArrayList<Class<?>> listNew = new ArrayList<Class<?>>();
        provider.createTable("table", listNew);
    }

    @Test
    public void getTableTest() throws IOException {
        Table table = provider.createTable("table", list);
        assertSame(provider.getTable("table"), table);
    }

    /*
     * @Test public void getTest() throws IOException { String tableName =
     * "table"; Table table = provider.createTable("table", list);
     * assertNotNull(table); assertEquals(tableName, table.getName());
     * provider.removeTable(tableName);
     * assertNull(provider.getTable(tableName)); }
     * 
     * @Test public void getTestTable() { Table table =
     * provider.createTable("Table"); assertNotNull(provider.getTable("Table"));
     * assertNull(provider.getTable("NotExistTable")); assertEquals(table,
     * provider.getTable("Table")); provider.removeTable("Table"); }
     * 
     * @Test(expected = IllegalStateException.class) public void
     * testRemoveNotExistsTable() { provider.removeTable("NotExistsTable"); }
     * 
     * @Test(expected = RuntimeException.class) public void testIncorrectName()
     * { provider.getTable("//"); }
     */
}
