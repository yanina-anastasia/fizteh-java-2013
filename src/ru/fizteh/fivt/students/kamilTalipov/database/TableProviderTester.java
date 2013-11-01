package ru.fizteh.fivt.students.kamilTalipov.database;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.FileNotFoundException;

public class TableProviderTester {
    public static TableProvider provider;

    @Rule
    static TemporaryFolder tmpDir = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() throws FileNotFoundException, DatabaseException {
        provider = new MultiFileHashTableProvider(tmpDir.getRoot().getAbsolutePath());
    }

    @Test(expected = FileNotFoundException.class)
    public void  illegalInitTest() throws FileNotFoundException, DatabaseException {
        TableProvider badProvider = new MultiFileHashTableProvider(tmpDir.getRoot().getAbsolutePath()
                                                                    + File.separator + "not_exist");
    }

    @Test
    public void createGetRemoveTest() {
        provider.createTable("Test");
        Assert.assertNotNull(provider.getTable("Test"));
        provider.removeTable("Test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTest() {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTest() {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTest() {
        provider.removeTable(null);
    }
}
