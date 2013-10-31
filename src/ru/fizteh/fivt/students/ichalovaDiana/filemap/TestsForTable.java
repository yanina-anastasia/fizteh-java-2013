package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.Table;

public class TestsForTable {
    
    static Path databaseDirectory;
    static Table table;
    
    @BeforeClass
    static public void createDatabase() throws IOException {
        databaseDirectory = Files.createTempDirectory(Paths.get(System.getProperty("user.dir")), null);
        Files.createDirectory(databaseDirectory.resolve("temp"));
    }
    
    @Before
    public void createTable() {
        table = new TableImplementation(databaseDirectory, "temp");
    }
    
    @AfterClass
    static public void deleteDatabase() throws IOException {
        Files.walkFileTree(databaseDirectory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e)
                    throws IOException {
                if (e == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    throw e;
                }
            }
        });
    }
    
    @Test
    public void getName() {
        Assert.assertTrue(table.getName() == "temp");
    }
    
    @Test
    public void getNonExistingKey() {
        Assert.assertTrue(table.get("not-exists") == null);
    }
    
    @Test
    public void putPutSameKey() {
        table.put("key1", "value1");
        Assert.assertEquals(table.put("key1", "value2"), "value1");
        table.remove("key1");
    }
    
    @Test
    public void putGetRemoveGet() {
        table.put("key1", "value1");
        Assert.assertEquals(table.get("key1"), "value1");
        table.remove("key1");
        Assert.assertTrue(table.get("key1") == null);
    }
    
    @Test
    public void removeNonExistingKey() {
        Assert.assertTrue(table.get("not-exists") == null);
    }
    
    @Test
    public void onePutSize() {
        table.put("key1", "value1");
        Assert.assertTrue(table.size() == 1);
        table.remove("key1");
    }
    
    @Test
    public void PutPutSameKeySize() {
        table.put("key1", "value1");
        table.put("key1", "value2");
        Assert.assertTrue(table.size() == 1);
        table.remove("key1");
    }
    
    @Test
    public void PutRollbackGet() {
        table.put("key1", "value1");
        Assert.assertTrue(table.rollback() == 1);
        Assert.assertTrue(table.get("key1") == null);
    }
    
    @Test
    public void PutCommitRollbackGet() {
        table.put("key1", "value1");
        Assert.assertTrue(table.commit() == 1);
        Assert.assertTrue(table.rollback() == 0);
        Assert.assertEquals(table.get("key1"), "value1");
        table.remove("key1");
    }
    

}