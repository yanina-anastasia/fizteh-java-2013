package ru.fizteh.fivt.students.nlevashov.junit;

import org.junit.*;
import static org.junit.Assert.*;

import ru.fizteh.fivt.students.nlevashov.factory.*;
import ru.fizteh.fivt.storage.strings.*;

public class JUnit {
    TableProviderFactory factory;
    TableProvider provider;
    Table table;

    @Before
    public void platformCreating() {
        factory = new MyTableProviderFactory();
        assertNotNull(factory);
        provider = factory.create("/home/nlevashov/desktop/java/test");
        assertNotNull(provider);
        table = provider.createTable("qwe");
        assertNotNull(table);
    }

    @After
    public void cleaning() {
        provider.removeTable("qwe");
    }

    @Test
    public void test1() {
        table.put("123", "456");
        assertEquals(table.get("123"), "456");
        assertNull(table.get("456"));
        assertEquals(table.commit(), 1);
        assertEquals(table.remove("123"), "456");
        assertNull(table.remove("123"));
        assertEquals(table.rollback(), 1);
        assertEquals(table.get("123"), "456");
    }
}
