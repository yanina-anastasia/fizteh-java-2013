package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.tests;

import org.junit.*;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.FileMapProvider;
import static org.junit.Assert.*;

public class FileMapProviderTester {
    static FileMapProvider provider;
    
    @Before
    public void init() {
        provider = new FileMapProvider(System.getProperty("fizteh.db.dir"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void invalidNameShouldFail() {
        TableProvider badProvider = new FileMapProvider("this should not contain /");
        badProvider.createTable("Should_not_get_here");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void badDirectoryShouldFail() {
        TableProvider badProvider = new FileMapProvider("asldkjfasdfajshdlfk");
        badProvider.createTable("Should_not_get_here");
    }
    
    @Test
    public void createAndGetForSameTableShouldBeEqual() {
        assertEquals(provider.createTable("table"), provider.getTable("table"));
    }
    
    @Test(expected = IllegalStateException.class)
    public void removeNonExistentShouldFail() {
        provider.removeTable("empty");
    }
    
    @Test
    public void createGetRemoveTest() {
        assertNotNull(provider.createTable("table"));
        assertNotNull(provider.getTable("table"));
        provider.removeTable("table");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createNullShouldFail() {
        provider.createTable(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getNullShouldFail() {
        provider.getTable(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void removeNullShouldFail() {
        provider.removeTable(null);
    }
    
    @After
    public void wipe() {
        provider.removeAllTables();
    }
}
