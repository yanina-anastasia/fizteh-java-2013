package ru.fizteh.fivt.students.annasavinova.filemap;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class TableRowTest {
    Storeable row;

    @Rule
    public TemporaryFolder root = new TemporaryFolder();

    @Before
    public void init() {
        DBaseProviderFactory fact = new DBaseProviderFactory();
        TableProvider prov = null;
        try {
            prov = fact.create(root.newFolder().toString());
        } catch (IOException e) {
            // not OK
        } catch (IllegalArgumentException e) {
            // not OK
        }
        ArrayList<Class<?>> columnTypes = new ArrayList<>();
        columnTypes.add(int.class);
        columnTypes.add(long.class);
        columnTypes.add(byte.class);
        columnTypes.add(float.class);
        columnTypes.add(double.class);
        columnTypes.add(boolean.class);
        columnTypes.add(String.class);
        Table table = null;
        try {
            table = prov.createTable("testTable", columnTypes);
        } catch (IOException e) {
            // not OK
        }
        row = prov.createFor(table);

    }

    @Test
    public void setColumnAtTestInt() {
        try {
            row.setColumnAt(0, 1);
        } catch (Throwable e) {
            fail("Unexpected exception");
        }
        assertSame(row.getColumnAt(0), 1);
    }

    @Test
    public void setColumnAtTestString() {
        try {
            String test = "qwerty";
            row.setColumnAt(6, test);
        } catch (Throwable e) {
            fail("Unexpected exception");
        }
        assertSame(row.getColumnAt(6), "qwerty");
    }

    @Test(expected = ColumnFormatException.class)
    public void setColumnAtIncorrectFormat() {
        String test = "Test";
        row.setColumnAt(0, test);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setColumnAtNegativeIndex() {
        row.setColumnAt(-1, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setColumnAtBigIndex() {
        row.setColumnAt(34, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnAtBigIndex() {
        row.getColumnAt(35);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnAtNegativeIndex() {
        row.getColumnAt(-5);
    }

    @Test
    public void getIntAtTest() {
        try {
            int a = 12;
            row.setColumnAt(0, a);
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Unexpected exception");
        }
        assertSame(row.getIntAt(0), 12);
    }

    @Test
    public void getStringAtTest() {
        try {
            row.setColumnAt(6, "12");
        } catch (Throwable e) {
            fail("Unexpected exception");
        }
        assertSame(row.getStringAt(6), "12");
    }
}
