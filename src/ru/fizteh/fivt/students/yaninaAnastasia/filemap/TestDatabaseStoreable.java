package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

public class TestDatabaseStoreable {
    Storeable storeable;

    @Before
    public void setUp() {
        List<Class<?>> columnTypes = new ArrayList<>();
        columnTypes.add(Integer.class);
        columnTypes.add(String.class);
        columnTypes.add(Long.class);
        columnTypes.add(Boolean.class);
        columnTypes.add(Double.class);
        columnTypes.add(Float.class);
        storeable = new DatabaseStoreable(columnTypes);
    }

    @After
    public void tearDown() throws Exception {
        storeable = null;
    }

    @Test
    public void putEmpty() {
        storeable.setColumnAt(1, "");
    }

    @Test
    public void putOnlyWhiteSpaces() {
        storeable.setColumnAt(1, "     ");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void putKeyBelowZero() {
        storeable.setColumnAt(-1, null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void putBigKey() {
        storeable.setColumnAt(10, null);
    }

    @Test
    public void correctTestOne() {
        storeable.setColumnAt(1, "One");
    }

    @Test
    public void correctTestTwo() {
        storeable.setColumnAt(0, 1994);
    }

    @Test(expected = ColumnFormatException.class)
    public void testIncorrectType() {
        storeable.setColumnAt(1, 5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testBoundsException() {
        storeable.setColumnAt(10, 12);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testBoundsExceptionMore() {
        storeable.setColumnAt(-10, 5);
    }

    @Test(expected = ColumnFormatException.class)
    public void testColumnFormatException() {
        storeable.setColumnAt(1, true);
    }

    @Test(expected = ColumnFormatException.class)
    public void testColumnFormatExceptionMore() {
        storeable.setColumnAt(0, "asdasd");
        storeable.getIntAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void testColumnFormatExceptionLongInt() {
        storeable.setColumnAt(1, new Long("100000000000"));
        storeable.getIntAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBounds() {
        storeable.getIntAt(10);
    }

    @Test(expected = ColumnFormatException.class)
    public void testColumnFormatString() {
        storeable.setColumnAt(1, new String("100000000000"));
        storeable.getIntAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetColumnFormatException() {
        storeable.getColumnAt(20);
    }

    @Test
    public void testGetColumnFormatNoException() {
        storeable.getColumnAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void testColumnFormatDouble() {
        storeable.setColumnAt(4, new Double("10000"));
        storeable.getDoubleAt(1);
    }

    @Test(expected = ColumnFormatException.class)
    public void testColumnFormatFloat() {
        storeable.setColumnAt(5, new Float("10000"));
        storeable.getDoubleAt(2);
    }

    @Test
    public void testColumnFormatBooleanCool() {
        storeable.setColumnAt(3, true);
        Assert.assertEquals(storeable.getBooleanAt(3), true);
    }

    @Test
    public void testColumnFormatDoubleCool() {
        storeable.setColumnAt(4, new Double("10000"));
        storeable.getDoubleAt(4);
    }

    @Test
    public void testColumnFormatFloatCool() {
        storeable.setColumnAt(5, new Float("10000"));
        storeable.getFloatAt(5);
    }

    @Test
    public void testColumnFormatLong() {
        storeable.setColumnAt(2, new Long("10"));
        storeable.getLongAt(2);
    }
}
