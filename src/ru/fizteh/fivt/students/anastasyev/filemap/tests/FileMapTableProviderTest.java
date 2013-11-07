/*
package ru.fizteh.fivt.students.anastasyev.filemap.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.anastasyev.filemap.FileMapTableProviderFactory;

import java.io.IOException;

import static junit.framework.Assert.*;

public class FileMapTableProviderTest {
    static TableProviderFactory tableProviderFactory;
    static TableProvider tableProvider;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void setUp() {
        tableProviderFactory = new FileMapTableProviderFactory();
    }

    @Before
    public void setTableProvider() throws IOException {
        tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        assertNotNull(tableProvider);
    }

    @Test
    public void testCreateTable() {
        assertNotNull(tableProvider.createTable("testTable"));
        assertNull(tableProvider.createTable("testTable"));
        tableProvider.removeTable("testTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullNamedTable() {
        tableProvider.createTable(null);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateBadNamedTable() {
        tableProvider.createTable("gew?ge>");
    }

    @Test
    public void testGetTable() {
        Table table = tableProvider.createTable("testGetTable");
        assertNotNull(tableProvider.getTable("testGetTable"));
        assertNull(tableProvider.getTable("testTableNotExists"));
        assertEquals(table, tableProvider.getTable("testGetTable"));
        tableProvider.removeTable("testGetTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullNamedTable() {
        tableProvider.getTable(null);
    }

    @Test
    public void testRemoveExistsTable() {
        assertNotNull(tableProvider.createTable("existsTable"));
        tableProvider.removeTable("existsTable");
        assertNull(tableProvider.getTable("existsTable"));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistsTable() {
        tableProvider.removeTable("notExistsTable");
    }

    @Test(expected = RuntimeException.class)
    public void testRemoveBadNamedTable() {
        tableProvider.removeTable("?");
    }
}
*/
