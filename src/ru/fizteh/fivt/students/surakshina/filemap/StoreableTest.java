package ru.fizteh.fivt.students.surakshina.filemap;

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

public class StoreableTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private NewTableProviderFactory factory;
    private TableProvider provider;
    private Table table;
    private ArrayList<Class<?>> list;
    private Storeable st;

    @Before
    public void create() throws IOException {
        list = new ArrayList<Class<?>>(5);
        list.add(0, Integer.class);
        list.add(1, String.class);
        list.add(2, Float.class);
        list.add(3, Double.class);
        list.add(4, Boolean.class);
        list.add(5, Byte.class);
        list.add(6, Long.class);
        factory = new NewTableProviderFactory();
        provider = factory.create(folder.newFolder().toString());
        table = provider.createTable("Table", list);
        st = new MyStoreable(table);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void negativeIndex() {
        st.getColumnAt(-1);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypeColumn() {
        st.setColumnAt(0, 1);
        st.setColumnAt(0, "key");
    }

    @Test
    public void testGetColumnAt() {
        st.setColumnAt(0, 10);
        st.setColumnAt(1, "value");
        st.setColumnAt(4, true);
        st.setColumnAt(3, 2.1);
        assertEquals(st.getColumnAt(0).getClass(), Integer.class);
        assertEquals(st.getColumnAt(1).getClass(), String.class);
        assertEquals(st.getColumnAt(4).getClass(), Boolean.class);
        assertEquals(st.getColumnAt(3).getClass(), Double.class);
    }

    @Test
    public void testGetIntAt() {
        st.setColumnAt(0, Integer.valueOf(1000));
        assertEquals(Integer.valueOf(1000), st.getIntAt(0));
    }

    @Test
    public void testGetFloatAt() {
        st.setColumnAt(2, Float.valueOf(1));
        assertEquals(Float.valueOf(1), st.getFloatAt(2));
    }

    @Test
    public void testGetBooleanAt() {
        st.setColumnAt(4, true);
        assertEquals(true, st.getBooleanAt(4));
    }

    @Test
    public void testGetStringAt() {
        st.setColumnAt(1, "sdsds  sdsf sfsf");
        assertEquals("sdsds  sdsf sfsf", st.getStringAt(1));
    }

    @Test(expected = ColumnFormatException.class)
    public void testIncorrectColumnType() {
        st.setColumnAt(0, 1022);
        st.setColumnAt(1, "val");
        st.setColumnAt(4, false);
        st.setColumnAt(3, 2.12);
        st.getIntAt(1);
        st.getBooleanAt(0);
        st.getDoubleAt(4);
        st.getIntAt(3);
    }

    @Test
    public void testGetDoubleAt() {
        st.setColumnAt(3, 1111.222);
        assertEquals(Double.valueOf(1111.222), st.getDoubleAt(3));
    }

    @Test
    public void testGetByteAt() {
        st.setColumnAt(5, (byte) 6);
        assertEquals(Byte.valueOf((byte) 6), st.getByteAt(5));
    }

    @Test
    public void testGetLongAt() {
        st.setColumnAt(6, Long.valueOf(1000000000));
        assertEquals(Long.valueOf(1000000000), st.getLongAt(6));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void negativeIndexGetLongAt() {
        st.getLongAt(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void negativeIndexGetIntAt() {
        st.getIntAt(15);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void negativeIndexGetFloatAt() {
        st.getFloatAt(20);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void negativeIndexGetStringAt() {
        st.getStringAt(-10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void negativeIndexGetDoubleAt() {
        st.getDoubleAt(-2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void negativeIndexGetBooleanAt() {
        st.getBooleanAt(450);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void negativeIndexGetByteAt() {
        st.getByteAt(45);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypeColumnGetIntAt() {
        st.setColumnAt(0, 1);
        st.getIntAt(1);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypeColumnGetFloatAt() {
        st.setColumnAt(2, 10);
        st.getFloatAt(1);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypeColumnGetDoubleAt() {
        st.setColumnAt(3, 10.111);
        st.getDoubleAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypeColumnGetStringAt() {
        st.setColumnAt(1, "llslss");
        st.getStringAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypeColumnGetByteAt() {
        st.setColumnAt(5, (byte) 6);
        st.getByteAt(4);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypeColumnGetBooleanAt() {
        st.setColumnAt(4, true);
        st.getBooleanAt(6);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypeColumnGetLongAt() {
        st.setColumnAt(6, 100000000);
        st.getLongAt(1);
    }
}
