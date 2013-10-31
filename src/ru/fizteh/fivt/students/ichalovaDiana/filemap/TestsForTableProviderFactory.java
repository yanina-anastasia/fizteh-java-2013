package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class TestsForTableProviderFactory {
    static Path databaseDirectory;
    static TableProviderFactory tableProviderFactory;
    
    @BeforeClass
    static public void createDatabase() throws IOException {
        databaseDirectory = Files.createTempDirectory(Paths.get(System.getProperty("user.dir")), null);
    }
    
    @Before
    public void createTableProviderFactory() {
        tableProviderFactory = new TableProviderFactoryImplementation();
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
}
