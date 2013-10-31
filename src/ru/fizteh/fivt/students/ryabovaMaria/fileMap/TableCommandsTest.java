package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;
import ru.fizteh.fivt.storage.strings.Table;

public class TableCommandsTest {
    File dir = new File("C:\\Users\\Маша\\my space\\javaWork\\table5");
    Table tempTable = new TableCommands(dir);
    
    @Test
    public void getNameTest() {
        assertTrue("Table name shold be \"table5\"", tempTable.getName().equals("table5"));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void putKeyEmpty() {
        tempTable.put("", "value");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void putValueEmpty() {
        tempTable.put("key", "");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void putIllegalKey() {
        tempTable.put("     ", "value");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void putIllegalValue() {
        tempTable.put("key", "        ");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void putKeyNull() {
        tempTable.put(null, "value");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void putValueNull() {
        tempTable.put("key", null);
    }
    
    @Test
    public void putCorretArgs() {
        String result = tempTable.put("key", "value");
        assertTrue("Result should be null", result == null);
    }
    
    @Test
    public void putKeyExists() {
        tempTable.put("good", "night");
        String last = tempTable.put("good", "day");
        assertTrue("Result shouldn't be null " + last, last.equals("night"));
    }
    
    @Test
    public void removeKeyNotExists() {
        assertTrue("Result should be null", tempTable.remove("notExists") == null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void removeKeyNull() {
        tempTable.remove(null);
    }
    
    @Test
    public void removeCorrectArgs() {
        tempTable.remove("key");
    }
    
    @Test
    public void sizeTest() {
        assertTrue("size should be 1", tempTable.size() == 1);
    }
    
    @Test
    public void commitTest() {
        tempTable.put("key", "value");
        tempTable.put("1", "1");
        tempTable.remove("key");
        assertTrue("Number of changes should be 1", tempTable.commit() == 1);
    }
    
    @Test
    public void rollBackTest() {
        tempTable.put("2", "2");
        tempTable.put("2", "3");
        assertTrue("Number of changes shold be 1", tempTable.rollback() == 1);
    }
}
