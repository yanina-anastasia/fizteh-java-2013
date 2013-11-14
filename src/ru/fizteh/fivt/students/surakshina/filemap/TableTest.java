package ru.fizteh.fivt.students.surakshina.filemap;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class TableTest {
    private TableProviderFactory factory;
    private TableProvider provider;
    private ArrayList<Class<?>> list;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void begin() throws IOException {
        factory = new NewTableProviderFactory();
        provider = factory.create(folder.newFolder().toString());
        list = new ArrayList<Class<?>>();
        list.add(Integer.class);
        list.add(Double.class);
        list.add(Float.class);
        list.add(String.class);
        list.add(Boolean.class);
        list.add(Byte.class);
        list.add(Long.class);

    }

    @Test(expected = IllegalArgumentException.class)
    public void checkKeyWithWhiteSpace() {
        Table table = null;
        try {
            table = provider.createTable("table", list);
        } catch (IOException e) {
            // ok
        }
        Storeable st = new MyStoreable(table);
        st.setColumnAt(0, 5);
        table.put("  sad ", st);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNull() {
        Table table = null;
        try {
            table = provider.createTable("table", list);
        } catch (IOException e) {
            // ok
        }
        table.get(null);
    }

    @Test
    public void getExistsKey() throws Exception {
        Table table = null;
        try {
            table = provider.createTable("table", list);
        } catch (IOException e) {
            // ok
        }
        Storeable st = new MyStoreable(table);
        st.setColumnAt(0, 1);
        assertNull(table.put("keyX", st));
        assertNotNull(table.get("keyX"));
        assertNotNull(table.remove("keyX"));
        assertNull(table.get("keyX"));
    }
}
