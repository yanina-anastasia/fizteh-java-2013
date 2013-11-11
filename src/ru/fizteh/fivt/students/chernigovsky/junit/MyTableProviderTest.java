package ru.fizteh.fivt.students.chernigovsky.junit;

import org.junit.*;

import java.io.File;

public class MyTableProviderTest {
    private ExtendedTableProvider tableProvider;
    File dbDirectory = new File("./myCoolDatabase");

    @Before
    public void setUp() {
        tableProvider = new MyTableProvider(dbDirectory, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNlShouldFail() {
        tableProvider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNlShouldFail() {
        tableProvider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNlShouldFail() {
        tableProvider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void removeNonExistingTableShouldFail() {
        tableProvider.removeTable("testNonExist");
    }

    @Test
    public void getNonExistingTableShouldFail() {
        Assert.assertNull("should be null", tableProvider.getTable("testNonExist"));
    }

    @Test
    public void getCreatedTable() {
        ExtendedTable created = tableProvider.createTable("testGet");
        ExtendedTable firstGet = tableProvider.getTable("testGet");
        ExtendedTable secondGet = tableProvider.getTable("testGet");
        Assert.assertSame("getting should returns the same table as create", created, firstGet);
        Assert.assertSame("getting the same table twice should return the same", firstGet, secondGet);
        tableProvider.removeTable("testGet");
    }

    @Test
    public void getRemovedTable() {
        tableProvider.createTable("testRemove");
        tableProvider.removeTable("testRemove");
        Assert.assertNull("should be null", tableProvider.getTable("testRemove"));
    }
}
