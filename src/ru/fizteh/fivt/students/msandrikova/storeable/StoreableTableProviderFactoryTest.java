package ru.fizteh.fivt.students.msandrikova.storeable;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class StoreableTableProviderFactoryTest {
    StoreableTableProviderFactory tableProviderFactory;

    @Before
    public void setUp() throws Exception {
        tableProviderFactory = new StoreableTableProviderFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate() throws IOException {
        tableProviderFactory.create(null);
    }

}
