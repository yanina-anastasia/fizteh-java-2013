package ru.fizteh.fivt.students.elenarykunova.filemap;

import static org.junit.Assert.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ru.fizteh.fivt.storage.structured.Storeable;

public class FilemapTest {

    private static Filemap table;
    private MyTableProvider prov;
    
    @Rule 
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void prepare() {
        File rootDir;
        try {
            rootDir = folder.newFolder("myroot");
            FileMapMain factory = new FileMapMain();
            prov = (MyTableProvider) factory.create(rootDir.getAbsolutePath());
            List<Class<?>> types = new ArrayList<Class<?>>(3);
            types.add(Integer.class);
            types.add(Double.class);
            types.add(String.class);
            table = (Filemap) prov.createTable("newTable", types);
        } catch (IOException e) {
            System.err.println("can't make tests");
        }
    }
    
    @Test
    public void testGetName() {
        assertNotNull(table.getName());
        assertEquals(table.getName(), "newTable");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testPutNullKey() {
        table.put(null, new MyStoreable(table));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testPutEmpty() {
        table.put("", new MyStoreable(table));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testPutNl() {
        table.put("                     ", new MyStoreable(table));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testPutNullVal() {
        table.put("alala", null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testGetNull() {
        table.get(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetEmpty() {
        table.get("");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testGetNl() {
        table.get("                 ");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRemoveNull() {
        table.remove(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRemoveEmpty() {
        table.remove("");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRemoveNl() {
        table.remove("              ");
    }

    @Test
    public void testPutGetRemove() throws IllegalArgumentException, ParseException {
        String valStr1 = "[1, 1.5, \"value\"]";
        Storeable val1 = prov.deserialize(table, valStr1);
        assertNull(table.put("key", val1));
        
        String valStr2 = "[1, 1.5, \"value2\"]";
        Storeable val2 = prov.deserialize(table, valStr2);
        assertNotNull(table.put("key", val2));
        
        String valStrGet = prov.serialize(table, table.get("key"));
        assertNotEquals(table.get("key"), val1);
        
        String valStrPut = prov.serialize(table, table.put("key", val1)); 
        assertEquals(table.put("key", val1), val2);
        assertNull(table.get("other_key"));
        
        String valStrRemove = prov.serialize(table, table.remove("key"));
        assertEquals(table.remove("key"), val1);
        
        assertNull(table.get("key"));
    }

    @Test
    public void testPutGetRemoveCyrillic() throws IllegalArgumentException, ParseException {
        String valStr1 = "[1, 1.5, значение1]";
        Storeable val1 = prov.deserialize(table, valStr1);
        assertNull(table.put("key", val1));
        
        String valStr2 = "[1, 1.5, значение2]";
        Storeable val2 = prov.deserialize(table, valStr2);
        assertNotNull(table.put("ключ", val2));
        
        String valStrGet = prov.serialize(table, table.get("ключ"));
        assertNotEquals(valStrGet, valStr1);
        
        String valStrPut = prov.serialize(table, table.put("ключ", val1)); 
        assertEquals(valStrPut, valStr2);
        assertNull(table.get("другой_ключ"));
        
        String valStrRemove = prov.serialize(table, table.remove("ключ"));
        assertEquals(valStrRemove, valStr1);
        
        assertNull(table.get("ключ"));
    }

    @Test
    public void testSizeCommitRollback() throws IllegalArgumentException, ParseException {
        int sz = 442;
        for (int i = 0; i < sz; i++) {
            String valStr = "[" + Integer.toString(i + 1) + ", 2.3, value_commit]";
            Storeable val = prov.deserialize(table, valStr);
            table.put(Integer.toString(i), val);
        }
        assertEquals(table.size(), sz);
        assertEquals(table.commit(), sz);
        assertEquals(table.size(), sz);
        assertEquals(table.rollback(), 0);
        for (int i = 0; i < sz; i++) {
            String valStr = "[" + Integer.toString(i + 1) + ", 2.3, value_commit]";
            String removeVal = prov.serialize(table, table.remove(Integer.toString(i)));
            assertEquals(removeVal, valStr);
        }        
        assertEquals(table.size(), 0);
        assertEquals(table.rollback(), sz);
    }

    @Test
    public void testRollback() throws IllegalArgumentException, ParseException {
        String valStr1 = "[0, 2, val1]";
        Storeable val1 = prov.deserialize(table, valStr1);
        table.put("11", val1);
        assertEquals(table.commit(), 1);
        table.remove("11");
        assertEquals(table.rollback(), 1);
        assertEquals(table.size(), 1);
        
        String valStr2 = "[4, 2, val2]";
        Storeable val2 = prov.deserialize(table, valStr2);
        String valPut = prov.serialize(table, table.put("11", val2)); 
        assertEquals(valPut, valStr1);
        table.put("11", val1);
        assertEquals(table.rollback(), 0);
        
        table.remove("11");
        assertEquals(table.rollback(), 1);
        
        String valGet = prov.serialize(table, table.get("11")); 
        assertEquals(valGet, valStr1);
        
        String valStrInit = "[0, 0.3, a]";
        Storeable valInit = prov.deserialize(table, valStrInit);

        table.put("key", valInit);
        table.commit();

        String valStr4 = "[0, 0.3, changed]";
        Storeable val4 = prov.deserialize(table, valStr4);

        table.put("key", val4);

        String valStr5 = "[0, 0.3, changed_again]";
        Storeable val5 = prov.deserialize(table, valStr5);

        table.put("key", val5);
        table.put("key", valInit);
        assertEquals(table.rollback(), 0);
        
        table.put("newKey", val1);
        table.remove("newKey");
        table.put("newKey", val2);
        table.remove("newKey");
        table.put("newKey", val2);
        table.remove("newKey");
        assertEquals(table.rollback(), 0);
    }

    @Test
    public void testCommit() throws IllegalArgumentException, ParseException {
        String valStr1 = "[0, 2, val1]";
        Storeable val1 = prov.deserialize(table, valStr1);
        table.put("11", val1);
        assertEquals(table.commit(), 1);
        
        String valStr2 = "[4, 2, val2]";
        Storeable val2 = prov.deserialize(table, valStr2);
        String valPut = prov.serialize(table, table.put("11", val2)); 
        assertEquals(valPut, valStr1);

        String valStrNew = "[0, 0.3, a]";
        Storeable valNew = prov.deserialize(table, valStrNew);

        table.put("k", valNew);
        assertEquals(2, table.commit());

        String valStr4 = "[0, 0.3, changed]";
        Storeable val4 = prov.deserialize(table, valStr4);

        table.put("k", val4);

        String valStr5 = "[0, 0.3, changed_again]";
        Storeable val5 = prov.deserialize(table, valStr5);

        table.put("k", val5);
        table.put("k", valNew);
        assertEquals(0, table.commit());

        table.remove("11");
        table.remove("k");
        assertEquals(2, table.commit());
        assertEquals(0, table.size());

        table.put("newKey", val1);
        table.remove("newKey");
        table.put("newKey", val2);
        table.remove("newKey");
        table.put("newKey", val2);
        table.remove("newKey");
        assertEquals(0, table.commit());

    }
}
