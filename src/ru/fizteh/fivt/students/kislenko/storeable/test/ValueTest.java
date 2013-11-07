package ru.fizteh.fivt.students.kislenko.storeable.test;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.kislenko.storeable.MyTable;
import ru.fizteh.fivt.students.kislenko.storeable.MyTableProvider;
import ru.fizteh.fivt.students.kislenko.storeable.Value;

import java.util.ArrayList;
import java.util.List;

public class ValueTest {
    private static MyTableProvider provider;
    private static MyTable table;
    private static Value testingValue;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        provider = new MyTableProvider();
        List<Class<?>> typelist = new ArrayList<Class<?>>();
        typelist.add(Integer.class);
        typelist.add(Long.class);
        typelist.add(Byte.class);
        typelist.add(Float.class);
        typelist.add(Double.class);
        typelist.add(Boolean.class);
        typelist.add(String.class);
        table = provider.createTable("table", typelist);
    }

    @Before
    public void setUp() throws Exception {
        testingValue = (Value) provider.createFor(table);
    }

    @Test
    public void testSetColumnAt() throws Exception {
        testingValue.setColumnAt(0, 100);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetWrongType() throws Exception {
        testingValue.setColumnAt(6, 1488);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetWrongColumnAt() throws Exception {
        // pay attention to the value!
        testingValue.setColumnAt(-1, "Repair the cradle!!!1");
    }

    @Test
    public void testGetBadIntAt() throws Exception {
        Assert.assertNull(testingValue.getColumnAt(0));
    }

    @Test
    public void testGetIntAt() throws Exception {
        testingValue.setColumnAt(0, 100);
        Assert.assertEquals(100, testingValue.getColumnAt(0));
    }


}
