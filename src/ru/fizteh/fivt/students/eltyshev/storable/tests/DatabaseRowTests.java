package ru.fizteh.fivt.students.eltyshev.storable.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.eltyshev.storable.database.DatabaseRow;

import java.util.ArrayList;
import java.util.List;

public class DatabaseRowTests {
    Storeable storeable;

    @Before
    public void setUp() {
        List<Class<?>> columnTypes = new ArrayList<>();
        columnTypes.add(Integer.class);
        columnTypes.add(String.class);
        storeable = new DatabaseRow(columnTypes);
    }

    @After
    public void tearDown() throws Exception {
        storeable = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNlValueShouldFail() {
        storeable.setColumnAt(1, "    ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putEmptyValueShouldFail() {
        storeable.setColumnAt(1, "");
    }
}
