package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.FileMapProviderFactory;

public class FileMapProviderFactoryTester {
    public TableProviderFactory factory;
    
    @Before
    public void init() {
        factory = new FileMapProviderFactory();
    }
    
    @Test
    public void legalCreateTest() {
        TableProvider provider = factory.create(System.getProperty("fizteh.db.dir"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void nullCreateTestShouldFail() {
        TableProvider provider = factory.create(null);
    }
}
