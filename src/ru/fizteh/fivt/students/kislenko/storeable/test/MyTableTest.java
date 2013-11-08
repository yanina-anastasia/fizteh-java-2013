package ru.fizteh.fivt.students.kislenko.storeable.test;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.students.kislenko.junit.test.Cleaner;
import ru.fizteh.fivt.students.kislenko.storeable.MyTable;
import ru.fizteh.fivt.students.kislenko.storeable.MyTableProvider;
import ru.fizteh.fivt.students.kislenko.storeable.MyTableProviderFactory;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;

public class MyTableTest {
    private static MyTableProvider provider;
    private static File databaseDir = new File("database");
    private static MyTable table;
    private static ArrayList<Class<?>> typeList = new ArrayList<Class<?>>();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        databaseDir.mkdir();
        provider = factory.create("database");
        typeList.add(String.class);
        typeList.add(Long.class);
        table = provider.createTable("table", typeList);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Cleaner.clean(databaseDir);
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals("table", table.getName());
    }

    @Test
    public void testPutNormalValue() throws Exception {
        Assert.assertNull(table.put("java", provider.deserialize(table, "[\"Dmitry\",58486067788038353]")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() throws Exception {
        table.put("nullKey", null);
    }

    @Test(expected = ParseException.class)
    public void testPutBadValue() throws Exception {
        table.put("badValueKey", provider.deserialize(table, "[\"wtf\",\"lol\"]"));
    }

    @Test
    public void testGetNormalValue() throws Exception {
        table.put("key1", provider.deserialize(table, "[\"stringValue1\",1]"));
        Assert.assertEquals("[\"stringValue1\",1]", provider.serialize(table, table.get("key1")));
    }
}
