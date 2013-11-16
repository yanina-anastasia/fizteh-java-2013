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
    public void testIntIncorrectType() {
        storeable.setColumnAt(1, 5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIntBoundsException() {
        storeable.setColumnAt(10, 12);
    }

    @Test(expected = ColumnFormatException.class)
    public void testStringIncorrectType() {
        storeable.setColumnAt(2, "testing in progress");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testStringBoundsException() {
        storeable.setColumnAt(10, "testing");
    }

    @Test(expected = ColumnFormatException.class)
    public void testIntColumnFormatExc() {
        storeable.setColumnAt(1, "asdasd");
        storeable.getIntAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIntOutOfBoundsExc() {
        storeable.getIntAt(10);
    }

    @Test
    public void testColumnFormatIntRight() {
        storeable.setColumnAt(0, Integer.valueOf(10));
        Assert.assertEquals(storeable.getIntAt(0), Integer.valueOf(10));
    }

    @Test(expected = ColumnFormatException.class)
    public void testStringColumnFormatExc() {
        storeable.setColumnAt(2, 1000);
        storeable.getStringAt(2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testStringOutOfBoundsExc() {
        storeable.getStringAt(10);
    }

    @Test
    public void testColumnFormatStringRight() {
        storeable.setColumnAt(1, String.valueOf("a"));
        Assert.assertEquals(storeable.getStringAt(1), String.valueOf("a"));
    }

    @Test(expected = ColumnFormatException.class)
    public void testLongColumnFormatExc() {
        storeable.setColumnAt(3, true);
        storeable.getLongAt(3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testLongOutOfBoundsExc() {
        storeable.getLongAt(10);
    }

    @Test
    public void testColumnFormatLongRight() {
        storeable.setColumnAt(2, Long.valueOf(10));
        Assert.assertEquals(storeable.getLongAt(2), Long.valueOf(10));
    }

    @Test(expected = ColumnFormatException.class)
    public void testBooleanColumnFormatExc() {
        storeable.setColumnAt(4, 10.5);
        storeable.getBooleanAt(4);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testBooleanOutOfBoundsExc() {
        storeable.getBooleanAt(10);
    }

    @Test
    public void testColumnFormatBooleanRight() {
        storeable.setColumnAt(3, Boolean.valueOf(true));
        Assert.assertEquals(storeable.getBooleanAt(3), Boolean.valueOf(true));
    }

    @Test(expected = ColumnFormatException.class)
    public void testDoubleColumnFormatExc() {
        storeable.setColumnAt(5, new Float(10.5));
        storeable.getDoubleAt(5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testDoubleOutOfBoundsExc() {
        storeable.getDoubleAt(10);
    }

    @Test
    public void testColumnFormatDoubleRight() {
        storeable.setColumnAt(4, Double.valueOf(10.4));
        Assert.assertEquals(storeable.getDoubleAt(4), Double.valueOf(10.4));
    }

    @Test(expected = ColumnFormatException.class)
    public void testFloatColumnFormatExc() {
        storeable.setColumnAt(1, "1000");
        storeable.getFloatAt(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testFloatOutOfBoundsExc() {
        storeable.getFloatAt(10);
    }

    @Test
    public void testColumnFormatFloatRight() {
        storeable.setColumnAt(5, Float.valueOf(10));
        Assert.assertEquals(storeable.getFloatAt(5), Float.valueOf(10));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testBoundsExceptionMore() {
        storeable.setColumnAt(-10, 5);
    }

    @Test(expected = ColumnFormatException.class)
    public void testColumnFormatException() {
        storeable.setColumnAt(1, Boolean.valueOf(true));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetIndexOutOfBoundsException() {
        storeable.getColumnAt(20);
    }

    @Test
    public void testGetColumnNoException() {
        storeable.setColumnAt(0, Integer.valueOf(10));
        Assert.assertEquals(storeable.getColumnAt(0), Integer.valueOf(10));
    }

    @Test
    public void testSetColumnNoException() {
        storeable.setColumnAt(1, String.valueOf("a"));
        Assert.assertEquals(storeable.getColumnAt(1), String.valueOf("a"));
    }
}
