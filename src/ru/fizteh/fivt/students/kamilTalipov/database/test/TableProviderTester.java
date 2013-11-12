package ru.fizteh.fivt.students.kamilTalipov.database.test;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.kamilTalipov.database.core.DatabaseException;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTableProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableProviderTester {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public MultiFileHashTableProvider provider;

    @Before
    public void initProvider() throws IOException, DatabaseException {
        provider = new MultiFileHashTableProvider(folder.getRoot().getAbsolutePath());
    }

    @Test(expected = DatabaseException.class)
    public void  illegalInitTest() throws IOException, DatabaseException {
        MultiFileHashTableProvider badProvider = new MultiFileHashTableProvider("gjfdou34923dkfjs");
    }

    @Test
    public void createGetRemoveTest() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(Double.class);

        provider.createTable("Test", types);
        Assert.assertNotNull(provider.getTable("Test"));
        provider.removeTable("Test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTest() throws IOException {
        provider.createTable(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTest() {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEmptyTableTest() {
        provider.getTable("     ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createIncorrectTableNameTest() {
        provider.getTable("??/?");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTest() {
        provider.removeTable(null);
    }
}
