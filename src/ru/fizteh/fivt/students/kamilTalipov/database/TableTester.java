package ru.fizteh.fivt.students.kamilTalipov.database;


import org.junit.*;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.FileNotFoundException;

public class TableTester {
    static Table table;

    @BeforeClass
    public static void beforeClass() {
        try {
            table = new MultiFileHashTable("/home/kamilz/DB", "Test");
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }

    }

    @Test
    public void getNameTest() {
        Assert.assertEquals(table.getName(), "Test");
    }

    @Test
    public void putGetCommitTest() {
        table.put("123", "abc");
        Assert.assertEquals(table.get("123"), "abc");
        Assert.assertEquals(table.size(), 1);
        Assert.assertEquals(table.commit(), 1);
    }

    @Test
    public void removeTest() {
        Assert.assertEquals(table.remove("fff"), null);
        table.put("qwe", "ggg");
        Assert.assertEquals(table.remove("qwe"), "ggg");
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


}
