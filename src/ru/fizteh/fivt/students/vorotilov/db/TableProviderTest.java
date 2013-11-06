package ru.fizteh.fivt.students.vorotilov.db;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

public class TableProviderTest {

    static VorotilovTableProviderFactory tableProviderFactory;
    static VorotilovTableProvider tableProvider;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void setUp() {
        tableProviderFactory = new VorotilovTableProviderFactory();
    }

    @Before
    public void setTableProvider() throws IOException {
        tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
    }

    @Test
    public void testCreateAndRemoveTable() {
        Assert.assertNotNull(tableProvider.createTable("newTable"));
        Assert.assertNull(tableProvider.createTable("newTable"));
        tableProvider.removeTable("newTable");
    }

    @Test
    public void testGetTable() {
        VorotilovTable table = tableProvider.createTable("newTable");
        Assert.assertNotNull(tableProvider.getTable("newTable"));
        Assert.assertEquals(table, tableProvider.getTable("newTable"));
        tableProvider.removeTable("newTable");
    }

    @Test
    public void testGetNotExistedTable() {
        Assert.assertNull(tableProvider.getTable("notExistedTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableWithNotValidName() {
        tableProvider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableWithNotValidName() {
        tableProvider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableWithEmptyName() {
        tableProvider.createTable(" ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableWithEmptyName() {
        tableProvider.removeTable("  ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableWithEmptyName() {
        tableProvider.getTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableWithNotValidName() {
        tableProvider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistedTable() {
        tableProvider.removeTable("notExistedTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadSymbolsInTableName() {
        tableProvider.createTable("//a");
    }

}
