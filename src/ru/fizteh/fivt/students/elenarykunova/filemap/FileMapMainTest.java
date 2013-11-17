package ru.fizteh.fivt.students.elenarykunova.filemap;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

//import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.TemporaryFolder;

public class FileMapMainTest {

    private File notExistingFile;
    private File existingFile;
    private File existingDir;
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Before
    public void prepare() {
        try {
            existingDir = folder.newFolder("existingDirPath");
        } catch (IOException e1) {
            System.err.println("can't make tests");
        }
        try {
            existingFile = folder.newFile("existingPath");
        } catch (IOException e1) {
            System.err.println("can't make tests");
        }
        notExistingFile = new File(folder + File.separator + "notExistingPath");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateNull() throws IllegalArgumentException, IOException {
        FileMapMain factory = new FileMapMain();
        factory.create(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateEmpty() {
        FileMapMain factory = new FileMapMain();
        try {
            factory.create("");
        } catch (IOException e) {
            fail("unexpected exception: expected IllegalArgumentException");
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateNl() {
        FileMapMain factory = new FileMapMain();
        try {
            factory.create("                         ");
        } catch (IOException e) {
            fail("unexpected exception: expected IllegalArgumentException");
        }
    }
    
    @Test
    public void testCreate() {
        FileMapMain factory = new FileMapMain();
        try {
            factory.create(existingDir.getParent());
        } catch (Exception e1) {
            fail("RootDir is correct, shouldn't fail");
        }
        
        try {
            factory.create(notExistingFile.getAbsolutePath());
            assertTrue(notExistingFile.exists() && notExistingFile.isDirectory());
        } catch (IllegalArgumentException e1) {
            //ok
        } catch (Exception e2) {
            fail("inizialize notExistingDir: expected IllegalArgumentException");
        }
                
        if (existingFile.isFile()) {
            try {
                factory.create(existingFile.getAbsolutePath());
                fail("inizialize TableProvider with file: expected IllegalArgumentException");
            } catch (IllegalArgumentException e1) {
                //ok
            } catch (Exception e2) {
                fail("inizialize TableProvider with file: expected IllegalArgumentException");
            }
        }
    }
}

