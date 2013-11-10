package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.tests;

import org.junit.*;
import static org.junit.Assert.*;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.FileMap;

public class FileMapTester {
    static Table table;

    @Before
    public void init() {
        table = new FileMap("Tested Table");
    }
    
    @Test
    public void getNameTest() {
        assertEquals(table.getName(), "Tested Table");
    }
    
    @Test
    public void noOpCommitTest() {
        table.put("key", "value");
        table.remove("key");
        assertEquals(table.commit(), 0);
    }
    
    @Test
    public void rollbackTest() {
        table.put("key", "value");
        table.commit();
        table.put("key", "other value");
        table.put("new_key", "new value");
        table.remove("non-existent_key");
        assertEquals(table.rollback(), 2);
        assertEquals(table.get("key"), "value");
        assertEquals(table.size(), 1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void illegalKeyShouldFail() {
        table.put("bad key", "value");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void emptyValueShouldFail() {
        table.put("key", "");
    }
    
    @Test
    public void legalValue() {
        table.put("key", "value with spaces inside");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void nullKeyShouldFail() {
        table.put(null, "value");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void nullValueShouldFail() {
        table.put("key", null);
    }

    @Test
    public void overWriteTest() {
        table.put("key", "old value");
        assertEquals(table.put("key", "new value"), "old value");
    }
    
    @Test
    public void putGetRemoveSizeCommitTest() {
        table.put("key", "value");
        assertEquals(table.get("key"), "value");
        assertEquals(table.size(), 1);
        assertEquals(table.commit(), 1);
        assertEquals(table.remove("key"), "value");
        assertNull(table.remove("key"));
        assertEquals(table.size(), 0);
    }
}
