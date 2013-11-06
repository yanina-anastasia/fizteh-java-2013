package ru.fizteh.fivt.students.ermolenko.multifilehashmap.test;

import org.junit.Test;
import ru.fizteh.fivt.students.ermolenko.multifilehashmap.MultiFileHashMapTableProviderFactory;

public class MultiFileHashMapTableProviderFactoryTest {

    private MultiFileHashMapTableProviderFactory tableProviderFactory = new MultiFileHashMapTableProviderFactory();

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        tableProviderFactory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmpty() throws Exception {
        tableProviderFactory.create("");
    }
}
