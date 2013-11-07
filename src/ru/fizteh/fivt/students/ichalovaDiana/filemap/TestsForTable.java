/*package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.Table;

public class TestsForTable {
    
    static Path databaseDirectory;
    static Table table;
    
    @BeforeClass
    public static void createDatabase() throws IOException {
        databaseDirectory = Files.createTempDirectory(Paths.get(System.getProperty("user.dir")), null);
    }
    
    @Before
    public void createTable() throws IOException {
        Files.createDirectory(databaseDirectory.resolve("temp"));
        table = new TableImplementation(databaseDirectory, "temp");
    }
    
    @After
    public void deleteTable() throws IOException {
        FileUtils.recursiveDelete(databaseDirectory.resolve("temp"));
    }
    
    @AfterClass
    public static void deleteDatabase() throws IOException {
        FileUtils.recursiveDelete(databaseDirectory);
    }
    
    @Test
    public void getName() {
        Assert.assertEquals("temp", table.getName());
    }
    
    @Test
    public void getNonExistingKey() {
        Assert.assertNull(table.get("not-exists"));
    }
    
    @Test
    public void putPutSameKey() {
        Assert.assertNull(table.put("key1", "value1"));
        Assert.assertEquals("value1", table.put("key1", "value2"));
    }
    
    @Test
    public void putGetRemoveGet() {
        Assert.assertNull(table.put("key1", "value1"));
        Assert.assertEquals("value1", table.get("key1"));
        Assert.assertEquals("value1", table.remove("key1"));
        Assert.assertNull(table.get("key1"));
    }
    
    @Test
    public void removeNonExistingKey() {
        Assert.assertNull(table.get("not-exists"));
    }
    
    @Test
    public void onePutSize() {
        Assert.assertNull(table.put("key1", "value1"));
        Assert.assertTrue(table.size() == 1);
    }
    
    @Test
    public void putPutSameKeySize() {
        Assert.assertNull(table.put("key1", "value1"));
        Assert.assertEquals("value1", table.put("key1", "value2"));
        Assert.assertTrue(table.size() == 1);
    }
    
    @Test
    public void putRollbackGet() {
        Assert.assertNull(table.put("key1", "value1"));
        Assert.assertTrue(table.rollback() == 1);
        Assert.assertNull(table.get("key1"));
    }
    
    @Test
    public void putCommitRollbackGet() {
        Assert.assertNull(table.put("key1", "value1"));
        Assert.assertTrue(table.commit() == 1);
        Assert.assertTrue(table.rollback() == 0);
        Assert.assertEquals("value1", table.get("key1"));
    }
}*/
