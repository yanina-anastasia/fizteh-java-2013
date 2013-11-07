package ru.fizteh.fivt.students.annasavinova.filemap;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DBaseProviderFactoryTest {
    static File rootDir;
    static File file;
    static TemporaryFolder root = new TemporaryFolder();

    @BeforeClass
    public static void createDir() {
        try {
            root.create();
            rootDir = root.newFolder("rootFolder");
            file = root.newFile("rootFolder" + File.separator + "filename");
        } catch (IOException e1) {
            System.err.println(e1.getMessage());
            e1.printStackTrace();
        }
    }

    @AfterClass
    public static void clean() {
        root.delete();
    }

    @Test
    public void testCreate() {
        System.setProperty("fizteh.db.dir", rootDir.getAbsolutePath());
        DBaseProviderFactory tmp = new DBaseProviderFactory();
        try {
            tmp.create(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            tmp.create(rootDir.getAbsolutePath() + "not_existing_name");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            tmp.create(file.getAbsolutePath());
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            assertNotNull(tmp.create(rootDir.getAbsolutePath()));
        } catch (Exception e) {
            fail("Unexpected exception");
        }

    }
}
