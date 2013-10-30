package ru.fizteh.fivt.students.kamilTalipov.database;

import org.junit.*;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.FileNotFoundException;

public class TableFactoryTester {
    static TableProviderFactory factory;

    @BeforeClass
    public static void beforeClass() {
        factory = new MultiFileHashTableFactory();
    }

    @Test
    public void normalCreateTest() throws FileNotFoundException, DatabaseException {
        factory.create("/home/kamilz/DB");
    }

    @Test(expected = FileNotFoundException.class)
    public void notFoundTest() throws FileNotFoundException, DatabaseException {
        factory.create("/kasds/asdfadas");
    }
}
