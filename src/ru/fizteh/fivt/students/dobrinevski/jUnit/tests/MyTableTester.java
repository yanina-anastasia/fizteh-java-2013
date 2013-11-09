package ru.fizteh.fivt.students.dobrinevski.jUnit.tests;

import org.junit.*;
import static org.junit.Assert.*;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.dobrinevski.jUnit.MyTable;
import ru.fizteh.fivt.students.dobrinevski.jUnit.MyTableProvider;

import java.lang.System;


public class MyTableTester {
    public static Table table;

    @Before
    public void init() {
        table = (new MyTableProvider(System.getProperty("fizteh.db.dir"))).createTable("Tested_Table");
    }

    @Test
    public void getNameTest() {
        assertEquals(table.getName(), "Tested Table");
    }

    @Test(expected = IllegalArgumentException.class)
    public void keyWithTabGiven() {
        table.put("\tk\te\ty\t", "value");
    }

    @Test
    public void ValueWithSpasesGiven() {
        table.put("key", " v a l u e ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void keyWithSpaceGiven() {
        table.put(" k e y ", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyValueGiven() {
        table.put("key", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullValueGiven() {
        table.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyKeyGiven() {
        table.put("", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullKeyGiven() {
        table.put(null, "value");
    }

    @Test
    public void BasicCommitTest() {
        table.put("key", "value");
        assertEquals(table.get("key"), "value");
        assertEquals(table.size(), 1);
        assertEquals(table.commit(), 1);
        assertEquals(table.remove("key"), "value");
        assertEquals(table.size(), 0);
        assertEquals(table.commit(), 1);
    }

    @Test
    public void singulaTest() {
        table.put("key", "value");
        table.remove("key");
        assertEquals(table.commit(), 0);
    }

    @Test
    public void rollbackTest() {
        assertNull(table.put("key1", "value1"));
        table.commit();
        assertEquals(table.put("key1", "value2"), "value1");
        assertNull(table.put("key2", "value3"));
        assertNull(table.remove("value1"));
        assertEquals(table.rollback(), 2);
        assertEquals(table.get("key1"), "value1");
        assertEquals(table.size(), 1);
    }

    @Test
    public void overWriteTest() {
        table.put("key", "value1");
        assertEquals(table.put("key", "value2"), "value1");
    }
}
