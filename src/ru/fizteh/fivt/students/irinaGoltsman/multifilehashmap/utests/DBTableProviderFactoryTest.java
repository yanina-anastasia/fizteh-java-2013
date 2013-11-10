package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.utests;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.DBTableProviderFactory;
import org.junit.Test;

public class DBTableProviderFactoryTest {
    TableProviderFactory factory = new DBTableProviderFactory();

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        factory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateErrorName() throws Exception {
        factory.create("\\/*&5ye");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmpty() throws Exception {
        factory.create("    ");
    }
}
