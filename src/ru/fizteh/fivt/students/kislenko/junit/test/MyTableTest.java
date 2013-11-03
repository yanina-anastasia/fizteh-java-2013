package ru.fizteh.fivt.students.kislenko.junit.test;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.kislenko.junit.MyTable;

public class MyTableTest {
    MyTable table;

    @Before
    public void setUp() throws Exception {
        table = new MyTable("test");
    }

    @Test
    public void testPutSimple() throws Exception {
        Assert.assertNull(table.put("a", "b"));
    }

    @Test
    public void testPutOverwrite() throws Exception {
        table.put("a", "b");
        Assert.assertEquals(table.put("a", "c"), "b");
    }

    @Test
    public void testRemoveNotExistingKey() throws Exception {
        Assert.assertNull(table.remove("someKey"));
    }

    @Test
    public void testRemoveSimple() throws Exception {
        table.put("a", "b");
        Assert.assertEquals(table.remove("a"), "b");
    }

    @Test
    public void testGetNotExistingKey() throws Exception {
        Assert.assertNull(table.get("nothing"));
    }

    @Test
    public void testGetSimple() throws Exception {
        table.put("a", "b");
        Assert.assertEquals(table.get("a"), "b");
        table.put("b", "c");
        Assert.assertEquals(table.get("b"), "c");
    }

    @Test
    public void testGetOverwritten() throws Exception {
        table.put("a", "b");
        table.put("a", "c");
        Assert.assertEquals(table.get("a"), "c");
    }

    @Test
    public void testGetRemoved() throws Exception {
        table.put("a", "b");
        table.put("c", "d");
        Assert.assertEquals(table.get("c"), "d");
        table.remove("c");
        Assert.assertNull(table.get("c"));
    }
}
