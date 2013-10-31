package ru.fizteh.fivt.students.elenarykunova.filemap;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.students.elenarykunova.shell.Shell;

public class FileMapMainTest {

    private File notExistingFile;
    private File existingFile;
    private File existingDir;


    @Before
    public void prepare() {
        notExistingFile = new File(System.getProperty("user.dir") + File.separator + "notExistingPath");
        if (notExistingFile.exists()) {
            notExistingFile.delete();
        }
        existingFile = new File(System.getProperty("user.dir") + File.separator + "existingPath");
        if (!existingFile.exists()) {
            try {
                existingFile.createNewFile();
            } catch (IOException e) {
                //uups
            }
        }
        existingDir = new File(System.getProperty("user.dir") + File.separator + "existingDirPath");
        if (!existingDir.exists()) {
            existingDir.mkdir();
        }
    }
    
    @Test
    public void testCreate() {
//        fail("Not yet implemented");
        FileMapMain factory = new FileMapMain();
        try {
            factory.create(null);
            fail("null: expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        } catch (Exception e1) {
            fail("null: expected IllegalArgumentException");
        }
        try {
            factory.create("");
            fail("empty: expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        } catch (Exception e1) {
            fail("empty: expected IllegalArgumentException");
        }
        try {
            factory.create("                ");
            fail("whitespaces: expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        } catch (Exception e1) {
            fail("whitespaces: expected IllegalArgumentException");
        }
        
        try {
            factory.create(System.getProperty("user.dir"));
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
    @After
    public void clean() {
        existingFile.delete();
        if (existingDir.exists()) {
            Shell sh = new Shell();
            sh.rm(existingDir.getAbsolutePath());
        }
        notExistingFile.delete();
    }
}
