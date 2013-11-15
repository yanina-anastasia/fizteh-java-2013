package ru.fizteh.fivt.students.annasavinova.filemap;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class DataBaseTest {
    DataBase test;
    TableProvider prov;

    @Rule
    public TemporaryFolder root = new TemporaryFolder();

    @Before
    public void initializeAnsdFillingBase() {
        DBaseProviderFactory fact = new DBaseProviderFactory();
        try {
            prov = fact.create(root.newFolder().toString());
        } catch (IllegalArgumentException | IOException e1) {
            // not OK
        }
        ArrayList<Class<?>> list = new ArrayList<>();
        list.add(String.class);
        try {
            test = (DataBase) prov.createTable("testTable", list);
        } catch (IOException e) {
            // not OK
        }
        Storeable row = prov.createFor(test);
        row.setColumnAt(0, "2");
        test.put("1", row);
        test.put("3", row);
        row.setColumnAt(0, "aasd2");
        test.put("la", row);
        row.setColumnAt(0, "значение с пробелами");
        test.put("z", row);
        row.setColumnAt(0, "l");
        test.put("we", row);
        row.setColumnAt(0, "значение_на_русском");
        test.put("ключ_на_русском", row);
    }

    @Test
    public void testGetName() {
        String name = test.getName();
        if (name == null) {
            fail("Null name");
        }
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() {
        Storeable tmp1 = test.get(null);
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testGetSpaces() {
        Storeable tmp2 = test.get("     ");
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testGetEmpty() {
        Storeable tmp3 = test.get("");
    }

    @Test
    public void testGetNotExistingKey() {
        try {
            assertNull("Get not_existing_key: Expected null", test.get("not_existing_key"));
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void testGet() {
        try {
            Storeable row = prov.createFor(test);
            row.setColumnAt(0, "2");
            test.put("1", row);
            assertSame(test.get("1").getStringAt(0), "2");
            test.put("3", row);
            assertSame(test.get("3").getStringAt(0), "2");
            row.setColumnAt(0, "aasd2");
            test.put("la", row);
            assertSame(test.get("la").getStringAt(0), "aasd2");
            row.setColumnAt(0, "значение с пробелами");
            test.put("z", row);
            assertSame(test.get("z").getStringAt(0), "значение с пробелами");
            row.setColumnAt(0, "l");
            test.put("we", row);
            assertSame(test.get("we").getStringAt(0), "l");
            row.setColumnAt(0, "значение_на_русском");
            test.put("ключ_на_русском", row);
            assertSame(test.get("ключ_на_русском").getStringAt(0), "значение_на_русском");
        } catch (Exception e) {
            fail("Unexpected exception");
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() {
        Storeable row = prov.createFor(test);
        row.setColumnAt(0, "value");
        test.put(null, row);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() {
        test.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyKey() {
        Storeable row = prov.createFor(test);
        row.setColumnAt(0, "value");
        test.put("", row);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutKeyWithSpaces() {
        Storeable row = prov.createFor(test);
        row.setColumnAt(0, "value");
        test.put("    ", row);
    }

    @Test
    public void testPut() {
        try {
            Storeable row1 = prov.createFor(test);
            row1.setColumnAt(0, "val1");
            assertNull(test.put("key", row1));
            Storeable row2 = prov.createFor(test);
            row2.setColumnAt(0, "val2");
            assertSame(test.put("key", row2), row1);
            row1.setColumnAt(0, "Значение на русском");
            assertSame(test.put("key", row1), row2);
            row2.setColumnAt(0, "val3");
            assertSame(test.put("key", row2), row1);
            assertSame(test.get("key"), row2);
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() {
        test.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmpty() {
        test.remove("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveSpaces() {
        test.remove("     ");
    }

    @Test
    public void testRemove() {
        try {
            Storeable row = prov.createFor(test);
            row.setColumnAt(0, "tmpValue");
            test.put("tmpKey", row);
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
            Storeable row = prov.createFor(test);
            row.setColumnAt(0, "2");
            test.put("1111", row);
            assertSame(test.size() - firstSize, 1);
            row.setColumnAt(0, "3");
            test.put("1111", row);
            assertSame(test.size() - firstSize, 1);
            test.remove("1111");
            assertSame(test.size() - firstSize, 0);
            row.setColumnAt(0, "2");
            test.put("1111", row);
            test.commit();
            assertSame(test.size() - firstSize, 1);
            test.put("2222", row);
            test.rollback();
            assertSame(test.size() - firstSize, 1);
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void testCommit() {
        try {
            Storeable row = prov.createFor(test);
            row.setColumnAt(0, "v0");
            test.put("k1", row);
            test.commit();
            row.setColumnAt(0, "v1");
            test.put("k1", row);
            row.setColumnAt(0, "v2");
            test.put("k1", row);
            assertSame(test.commit(), 1);
            assertSame(test.get("k1").getStringAt(0), "v2");
            test.remove("k1");
            assertSame(test.commit(), 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception " + e.getMessage());
        }
    }

    @Test
    public void testCountChanges() {
        try {
            test.commit();
            Storeable row = prov.createFor(test);
            row.setColumnAt(0, "v1");
            test.put("z1", row);
            row.setColumnAt(0, "v2");
            test.put("z1", row);
            assertSame(test.countChanges(), 1);
            row.setColumnAt(0, "v1");
            test.put("z2", row);
            test.remove("z2");
            assertSame(test.countChanges(), 1);
            test.commit();
            assertSame(test.countChanges(), 0);
            row.setColumnAt(0, "v3");
            test.put("z1", row);
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
            Storeable row = prov.createFor(test);
            row.setColumnAt(0, "v1");
            test.put("r1", row);
            test.put("r2", row);
            assertSame(test.rollback(), 2);
            assertNull(test.get("r1"));
            assertNull(test.get("r2"));
            assertSame(startSize, test.size());
            test.put("r1", row);
            test.put("r2", row);
            assertSame(startSize + 2, test.size());
            test.commit();
            assertSame(test.rollback(), 0);
            assertSame(test.get("r1").getStringAt(0), "v1");
            assertSame(test.get("r2").getStringAt(0), "v1");
            assertSame(startSize + 2, test.size());
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }
}
