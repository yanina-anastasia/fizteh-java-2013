package ru.fizteh.fivt.students.anastasyev.filemap.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.anastasyev.filemap.FileMapTableProviderFactory;

import static junit.framework.Assert.*;

public class FileMapTableProviderTest {
    static TableProviderFactory tableProviderFactory;
    static TableProvider tableProvider;

    @BeforeClass
    public static void setUp() {
        //System.getProperties().setProperty("fizteh.db.dir", "C:\\Users\\qBic
        // \\Documents\\GitHub\\fizteh-java-2013\\src\\ru\\fizteh\\fivt\\students\\anastasyev\\filemap\\db.dir");
        tableProviderFactory = new FileMapTableProviderFactory();
        tableProvider = tableProviderFactory.create(System.getProperty("fizteh.db.dir"));
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
