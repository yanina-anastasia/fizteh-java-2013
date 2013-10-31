package ru.fizteh.fivt.students.annasavinova.filemap;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DataBaseProviderTest {
    static File rootDir = new File(System.getProperty("user.dir") + File.separatorChar + "tmpRootDir");
    DataBaseProvider test;

    @BeforeClass
    public static void createDir() {
        rootDir.mkdir();
        File table1 = new File(rootDir.getAbsolutePath() + File.separator + "testBase1");
        File table2 = new File(rootDir.getAbsolutePath() + File.separator + "testBase2");
        table1.mkdir();
        table2.mkdir();
    }

    @Before
    public void initialize() {
        test = new DataBaseProvider(rootDir.getAbsolutePath());
    }

    @AfterClass
    public static void clean() {
        DataBaseProvider.doDelete(rootDir);
    }

    @SuppressWarnings("unused")
    @Test
    public void testDataBaseProvider() {
        try {
            DataBaseProvider tmp1 = new DataBaseProvider(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            DataBaseProvider tmp2 = new DataBaseProvider("");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            DataBaseProvider tmp3 = new DataBaseProvider("./not_existing_directory");
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalStateException");
        }
    }

    @Test
    public void testCheckTableName() {
        try {
            assertFalse(test.checkTableName(".qwer"));
            assertFalse(test.checkTableName("..qwer"));
            assertFalse(test.checkTableName("/qwer"));
            assertFalse(test.checkTableName("/"));
            assertFalse(test.checkTableName("\\qw"));
            assertFalse(test.checkTableName(";qwer"));
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void testGetTable() {
        try {
            test.getTable(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalErgumentException");
        }

        try {
            test.getTable("");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalErgumentException");
        }

        try {
            assertNull(test.getTable("not_existing_table"));
            assertNotNull(test.getTable("testBase1"));
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void testCreateTable() {
        try {
            test.createTable(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalErgumentException");
        }

        try {
            test.createTable("");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalErgumentException");
        }

        try {
            assertNotNull(test.createTable("tmpTable"));
            assertNull(test.createTable("tmpTable"));
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void testRemoveTable() {
        try {
            test.removeTable(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalErgumentException");
        }

        try {
            test.removeTable("");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalErgumentException");
        }

        try {
            test.removeTable("not_existing_table");
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalStateException");
        }

        try {
            test.createTable("table_for_removing");
            test.removeTable("table_for_removing");
            assertNull(test.getTable("table_for_removing"));
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

}
