package ru.fizteh.fivt.students.kamilTalipov.database;


import org.junit.*;

import java.io.FileNotFoundException;
import java.io.IOException;

public class TableTester {
    static MultiFileHashTable table;

    @BeforeClass
    public static void beforeClass() {
        try {
            table = new MultiFileHashTable(System.getProperty("user.dir"), "Test");
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    @Test
    public void getNameTest() {
        Assert.assertEquals(table.getName(), "Test");
    }

    @Test
    public void putGetCommitTest() {
        table.put("123", "hello");
        Assert.assertEquals(table.get("123"), "hello");
        Assert.assertEquals(table.size(), 1);
        Assert.assertEquals(table.commit(), 1);
    }

    @Test
    public void removeTest() {
        Assert.assertEquals(table.remove("fff"), null);
        table.put("qwe", "ggg");
        Assert.assertEquals(table.remove("qwe"), "ggg");
        Assert.assertEquals(table.rollback(), 1);
    }

    @Test
    public void rollbackTest() {
        Assert.assertEquals(table.put("fits", "1235"), null);
        Assert.assertEquals(table.rollback(), 1);
        Assert.assertEquals(table.get("fits"), null);
    }

    @Test
    public void overwriteTest() {
        table.put("123", "abc");
        Assert.assertEquals(table.put("123", "xyz"), "abc");
        table.commit();
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullTest() {
        table.put(null, "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTest() {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTest() {
        table.remove(null);
    }

    @AfterClass
    public static void afterClass() {
        try {
            table.removeTable();
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
        }
    }
}
