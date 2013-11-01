package ru.fizteh.fivt.students.kamilTalipov.database;

import org.junit.*;

import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;
import java.io.FileNotFoundException;

public class TableFactoryTester {
    static TableProviderFactory factory;

    @Rule
    static TemporaryFolder tmpDir = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() {
        factory = new MultiFileHashTableFactory();
    }

    @Test
    public void normalCreateTest() throws FileNotFoundException, DatabaseException {
        factory.create(tmpDir.getRoot().getAbsolutePath());
    }

    @Test(expected = FileNotFoundException.class)
    public void notFoundTest() throws FileNotFoundException, DatabaseException {
        factory.create(tmpDir.getRoot().getAbsolutePath() + File.separator + "not_exist");
    }
}
