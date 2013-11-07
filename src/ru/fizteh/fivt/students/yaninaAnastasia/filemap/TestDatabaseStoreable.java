package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import org.junit.After;
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
        storeable = new DatabaseStoreable(columnTypes);
    }

    @After
    public void tearDown() throws Exception {
        storeable = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void putEmpty() {
        storeable.setColumnAt(1, "");
    }

    @Test(expected = IllegalArgumentException.class)
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
        storeable.setColumnAt(0, 1995);
    }

    @Test(expected = ColumnFormatException.class)
    public void testIncorrectType() {
        storeable.setColumnAt(1, 2);
    }
}
