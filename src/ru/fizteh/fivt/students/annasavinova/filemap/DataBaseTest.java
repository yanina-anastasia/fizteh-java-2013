package ru.fizteh.fivt.students.annasavinova.filemap;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DataBaseTest {
    DataBase test;
    static File rootDir = new File(System.getProperty("user.dir") + File.separatorChar + "tmpRootDir");

    @BeforeClass
    public static void createDir() {
        File table = new File(rootDir.getAbsolutePath() + File.separator + "testBase");
        rootDir.mkdir();
        table.mkdir();
    }

    @Before
    public void initializeAnsdFillingBase() {
        test = new DataBase("testBase", rootDir.getAbsolutePath());
        test.put("1", "2");
        test.put("3", "2");
        test.put("la", "aasd2");
        test.put("z", "значение с пробелами");
        test.put("we", "l");
        test.put("ключ_на_русском", "значение_на_русском");
    }

    @AfterClass
    public static void clean() {
        DataBaseProvider.doDelete(rootDir);
    }

    @SuppressWarnings("unused")
    @Test
    public void testDataBase() {
        try {
            DataBase test = new DataBase(null, rootDir.getAbsolutePath());
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            DataBase test = new DataBase("testBase", null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            DataBase test = new DataBase("not_existing_table", rootDir.getAbsolutePath());
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalStateException");
        }

        try {
            DataBase test = new DataBase("lala", rootDir.getAbsolutePath() + File.separator + "not_existing_name");
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalStateException");
        }
    }

    @Test
    public void testGetName() {
        String name = test.getName();
        if (name == null) {
            fail("Null name");
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void testGet() {
        try {
            String tmp1 = test.get(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            String tmp2 = test.get("     ");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            String tmp3 = test.get("");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            assertNull("Get not_existing_key: Expected null", test.get("not_existing_key"));
            assertSame(test.get("1"), "2");
            assertSame(test.get("3"), "2");
            assertSame(test.get("la"), "aasd2");
            assertSame(test.get("z"), "значение с пробелами");
            assertSame(test.get("we"), "l");
            assertSame(test.get("ключ_на_русском"), "значение_на_русском");
        } catch (Exception e) {
            fail("Unexpected exception");
        }

    }

    @Test
    public void testPut() {
        try {
            test.put(null, "value");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            test.put("key", null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            test.put("key", "");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            test.put("key", "     ");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            test.put("", "value");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            test.put("    ", "value");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            assertNull(test.put("key", "val1"));
            assertSame(test.put("key", "val2"), "val1");
            assertSame(test.put("key", "Значение на русском"), "val2");
            assertSame(test.put("key", "val3"), "Значение на русском");
            assertSame(test.get("key"), "val3");
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void testRemove() {
        try {
            test.remove(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            test.remove("");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            test.remove("     ");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }

        try {
            test.put("tmpKey", "tmpValue");
            test.remove("tmpKey");
            assertNull(test.get("tmpKey"));
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void testSize() {
        try {
            int firstSize = test.size();
            assertTrue(firstSize >= 0);
            test.put("1111", "2");
            assertSame(test.size() - firstSize, 1);
            test.put("1111", "3");
            assertSame(test.size() - firstSize, 1);
            test.remove("1111");
            assertSame(test.size() - firstSize, 0);
            test.put("1111", "2");
            test.commit();
            assertSame(test.size() - firstSize, 1);
            test.put("2222", "2");
            test.rollback();
            assertSame(test.size() - firstSize, 1);
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void testCommit() {
        try {
            test.put("k1", "v0");
            test.commit();
            test.put("k1", "v1");
            test.put("k1", "v2");
            assertSame(test.commit(), 1);
            assertSame(test.get("k1"), "v2");
            test.remove("k1");
            assertSame(test.commit(), 1);
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void testCountChanges() {
        try {
            test.commit();
            test.put("z1", "v1");
            test.put("z1", "v2");
            test.put("z2", "v1");
            test.remove("z2");
            assertSame(test.countChanges(), 1);
            test.commit();
            assertSame(test.countChanges(), 0);
            test.remove("z1");
            assertSame(test.countChanges(), 1);
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void testRollback() {
        try {
            test.commit();
            assertSame(test.rollback(), 0);
            int startSize = test.size();
            test.put("r1", "v1");
            test.put("r2", "v1");
            assertSame(test.rollback(), 2);
            assertNull(test.get("r1"));
            assertNull(test.get("r2"));
            assertSame(startSize, test.size());
            test.put("r1", "v1");
            test.put("r2", "v1");
            assertSame(startSize + 2, test.size());
            test.commit();
            assertSame(test.rollback(), 0);
            assertSame(test.get("r1"), "v1");
            assertSame(test.get("r2"), "v1");
            assertSame(startSize + 2, test.size());
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }
}
