package ru.fizteh.fivt.students.irinapodorozhnaya.storeable.junit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.CommandRemove;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.MyTableProviderFactory;

public class TableTest {


    private static final String DATA_BASE_DIR = "./src/ru/fizteh/fivt/students/irinapodorozhnaya/test";
    private File f = new File(DATA_BASE_DIR);
    private Table testTable;
    private TableProvider provider;
    private List<Class<?>> columnType = new ArrayList<>();
    private Storeable val1;
    private Storeable val2;
    
    @Before
    public void setUp() throws Exception {
        f.mkdirs();
        provider = new MyTableProviderFactory().create(DATA_BASE_DIR);
        columnType.add(Integer.class);
        columnType.add(Double.class);
        testTable = provider.createTable("table", columnType);
        List<Object> values = new ArrayList<>();
        values.add(1);
        values.add(10.0);
        val1 = provider.createFor(testTable, values);
        values.set(0, 2);
        val2 = provider.createFor(testTable, values);
    }
    
    @After
    public void tearDown() throws Exception {
        CommandRemove.deleteRecursivly(f);
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals(testTable.getName(), "table");
    }

    @Test
    public void testColumnsCount() throws Exception {
        Assert.assertEquals(testTable.getColumnsCount(), 2);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testIllegalColumn() throws Exception {
        testTable.getColumnType(5);
    }

    @Test
    public void testColumnsType() throws Exception {
        Assert.assertEquals(testTable.getColumnType(0), Integer.class);
        Assert.assertEquals(testTable.getColumnType(1), Double.class);        
    }

    
    @Test(expected = IllegalArgumentException.class)
    public void testGetWithNull() throws Exception {
        testTable.get(null);
    }

    @Test
    public void testGet() throws Exception {
        testTable.put("getKey", val1);
        Assert.assertEquals(testTable.get("getKey"), val1);
        Assert.assertNull(testTable.get("key"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNull() throws Exception {
        testTable.put(null, null);
        testTable.put("nullValue", null);
    }

    @Test
    public void testPut() throws Exception {
        Assert.assertNull(testTable.put("put", val1));
        Assert.assertEquals(testTable.put("put", val2), val1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutWhiteSpace() throws Exception {
        testTable.put("put with space", val1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWhiteSpace() throws Exception {
        testTable.get("put with space");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveWithNull() throws Exception {
        testTable.remove(null);
    }

    @Test
    public void testRemove() throws Exception {
        testTable.put("remove", val1);
        Assert.assertNull(testTable.remove("notRemove"));
        Assert.assertEquals(testTable.remove("remove"), val1);
    }

    @Test
    public void testSize() throws Exception {
        testTable.put("size1", val1);
        Assert.assertEquals(testTable.size(), 1);
        testTable.put("size2", val2);
        testTable.commit();
        testTable.put("size2", val1);
        Assert.assertEquals(testTable.size(), 2);
        testTable.remove("size2");
        Assert.assertEquals(testTable.size(), 1);
        testTable.rollback();
        Assert.assertEquals(testTable.size(), 2);
    }

    @Test
    public void testCommit() throws Exception {
        testTable.put("commit1", val1);
        testTable.put("commit2", val1);
        Assert.assertEquals(testTable.commit(), 2);
        testTable.put("commit3", val2);
        testTable.put("commit2", val2);
        testTable.rollback();
        Assert.assertEquals(testTable.get("commit2"), val1);
        Assert.assertNull(testTable.get("commit3"));
    }

    @Test
    public void testRollback() throws Exception {
        testTable.put("rollback1", val1);
        testTable.put("rollback2", val1);
        testTable.commit();
        testTable.put("rollback1", val2);
        testTable.put("rollback3", val2);
        Assert.assertEquals(testTable.rollback(), 2);
        Assert.assertNull(testTable.get("rollback3"));
        Assert.assertEquals(testTable.get("rollback1"), val1);
    }

    @Test (expected = ColumnFormatException.class)
    public void incorrectStoreableSize() throws Exception{
        List<Object> list = new ArrayList<>();
        columnType.add(String.class);
        list.add(5);
        list.add(4.0);
        list.add("String");
        Table t = provider.createTable("table2", columnType);
        Storeable s = provider.createFor(t, list);
        provider.removeTable("table2");
        testTable.put("key", s);
    }

    @Test (expected = ColumnFormatException.class)
    public void lessStoreableSize() throws Exception{
        List<Object> list = new ArrayList<>();
        columnType.remove(1);
        list.add(5);
        Table t = provider.createTable("table2", columnType);
        Storeable s = provider.createFor(t, list);
        provider.removeTable("table2");
        testTable.put("key", s);
    }

    @Test (expected = ColumnFormatException.class)
    public void incorrectStoreableType() throws Exception{
        List<Object> list = new ArrayList<>();
        columnType.set(1, String.class);
        list.add(5);
        list.add("String");
        Table t = provider.createTable("table2", columnType);
        Storeable s = provider.createFor(t, list);
        provider.removeTable("table2");
        testTable.put("key", s);
    }
}
