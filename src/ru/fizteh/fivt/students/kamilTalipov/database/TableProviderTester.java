package ru.fizteh.fivt.students.kamilTalipov.database;

import org.junit.*;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.FileNotFoundException;

public class TableProviderTester {
    public static MultiFileHashTableProvider provider;

    @BeforeClass
    public static void beforeClass() throws FileNotFoundException, DatabaseException {
        provider = new MultiFileHashTableProvider(System.getProperty("user.dir") + File.separator + "Test");
    }

    @Test(expected = DatabaseException.class)
    public void  illegalInitTest() throws FileNotFoundException, DatabaseException {
        TableProvider badProvider = new MultiFileHashTableProvider("gjfdou34923dkfjs");
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

    @AfterClass
    public static void afterClass() {
        provider.remove();
    }
}
