package ru.fizteh.fivt.students.annasavinova.filemap;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DBaseProviderFactoryTest {
    static File rootDir = new File(System.getProperty("user.dir") + File.separatorChar + "tmpRootDir");
    static File file = new File(rootDir.getAbsolutePath() + "filename");

    @BeforeClass
    public static void createDir() {
        rootDir.mkdir();
        try {
            file.createNewFile();
        } catch (IOException e) {
            // cannot create new file
        }
    }

    @AfterClass
    public static void clean() {
        DataBaseProvider.doDelete(rootDir);
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
