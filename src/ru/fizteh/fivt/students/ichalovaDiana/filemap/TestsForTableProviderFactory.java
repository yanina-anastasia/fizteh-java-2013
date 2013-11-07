/*package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class TestsForTableProviderFactory {
    static Path databaseDirectory;
    static TableProviderFactory tableProviderFactory;
    
    @BeforeClass
    public static void createDatabase() throws IOException {
        databaseDirectory = Files.createTempDirectory(Paths.get(System.getProperty("user.dir")), null);
    }
    
    @Before
    public void createTableProviderFactory() {
        tableProviderFactory = new TableProviderFactoryImplementation();
    }
    
    @AfterClass
    public static void deleteDatabase() throws IOException {
        FileUtils.recursiveDelete(databaseDirectory);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createNonExisting() {
        tableProviderFactory.create("not-exists");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createNullDir() {
        tableProviderFactory.create(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createEmptyDir() {
        tableProviderFactory.create("");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createWhitespaceDir() {
        tableProviderFactory.create("\t \n  ");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createDirWithSlash() {
        tableProviderFactory.create("../table");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createDirWithBackSlash() {
        tableProviderFactory.create("..\table");
    }
}*/
