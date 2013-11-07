/*package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.TableProvider;

public class TestsForTableProvider {
    
    static Path databaseDirectory;
    static TableProvider tableProvider;
    
    @BeforeClass
    public static void createDatabase() throws IOException {
        databaseDirectory = Files.createTempDirectory(Paths.get(System.getProperty("user.dir")), null);
    }
    
    @Before
    public void createTableProvider() {
        tableProvider = new TableProviderImplementation(databaseDirectory);
    }
    
    @AfterClass
    public static void deleteDatabase() throws IOException {
        FileUtils.recursiveDelete(databaseDirectory);
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void getTableNullTableName() {
        tableProvider.getTable(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getTableNullByteTableName() {
        tableProvider.getTable("a\0b");
    }
    
    public void getNonExistingTable() {
        Assert.assertNull(tableProvider.getTable("not-exists"));
    }
    
    @Test
    public void getTableForSameNamesShouldReturnSameObject() {
        tableProvider.createTable("temp");
        Assert.assertEquals(tableProvider.getTable("temp"), tableProvider.getTable("temp"));
        tableProvider.removeTable("temp");
    }
    
    
    @Test
    public void createTableForExistingTableReturnsNull() {
        tableProvider.createTable("temp");
        Assert.assertNull(tableProvider.createTable("temp"));
        tableProvider.removeTable("temp");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createTableNullTableName() {
        tableProvider.createTable(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createTableNullByteTableName() {
        tableProvider.createTable("a\0b");
    }
    
    
    @Test(expected = IllegalStateException.class)
    public void removeNonExistingTable() {
        tableProvider.removeTable("not-exists");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void removeTableNullTableName() {
        tableProvider.removeTable(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void removeTableNullByteTableName() {
        tableProvider.removeTable("a\0b");
    }
    
    
    @Test 
    public void createRemove() {
        tableProvider.createTable("temp");
        Assert.assertNotNull(tableProvider.getTable("temp"));
        tableProvider.removeTable("temp");
        Assert.assertNull(tableProvider.getTable("temp"));
    }
}*/
