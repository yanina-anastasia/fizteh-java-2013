package ru.fizteh.fivt.students.kamilTalipov.database;

import org.junit.*;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;
import java.io.FileNotFoundException;

public class TableFactoryTester {
    static MultiFileHashTableFactory factory;

    @BeforeClass
    public static void beforeClass() {
        factory = new MultiFileHashTableFactory();
    }

    @Test
    public void normalCreateTest() throws FileNotFoundException, DatabaseException {
        MultiFileHashTableProvider provider = factory.create(System.getProperty("user.dir")
                                                            + File.separator + "Test");
        provider.remove();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullCreateTest() throws FileNotFoundException, DatabaseException {
        factory.create(null);
    }

}
