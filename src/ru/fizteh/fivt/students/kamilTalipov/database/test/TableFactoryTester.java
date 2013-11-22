package ru.fizteh.fivt.students.kamilTalipov.database.test;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTableFactory;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTableProvider;

import java.io.IOException;

public class TableFactoryTester {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void normalCreateTest() throws IOException {
        MultiFileHashTableFactory factory = new MultiFileHashTableFactory();
        MultiFileHashTableProvider provider = factory.create(folder.getRoot().getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullCreateTest() throws IOException {
        MultiFileHashTableFactory factory = new MultiFileHashTableFactory();
        MultiFileHashTableProvider provider = factory.create(null);
    }
}
