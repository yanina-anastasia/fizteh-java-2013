package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import java.util.HashMap;
import java.util.Random;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class TableCommandsTest {
    TableProviderFactory tempFactory = new MyTableProviderFactory();
    TableProvider tempTableProvider;
    Table tempTable;
    File createdFolder;
    HashMap<String, String> tempMap = new HashMap<String, String>();
    String firstKey;
    String firstValue;
    int length;
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Before
    public void initTempTableProvider() {
        createdFolder = tempFolder.newFolder("workFolder");
        tempTableProvider = tempFactory.create(createdFolder.toString());
        assertNotNull("Object of TableProvider shouldn't be null",tempTableProvider);
        tempTable = tempTableProvider.createTable("table");
        assertNotNull("Object of Table shouldn't be null", tempTable);
    }
    
    @After
    public void deleteAll() {
        createdFolder.delete();
    }
    
    @Test
    public void getNameTest() {
        assertTrue("Table name shold be \"table\"", tempTable.getName().equals("table"));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void getNull() {
        tempTable.get(null);
    }
    
    @Test
    public void getNotExistsKey() {
        String value = tempTable.get("notExists");
        assertNull("Value should be null", value);
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
        tempTable.put("\n  ", "value");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void putIllegalValue() {
        tempTable.put("key", "new \n value");
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
        assertNull("Result should be null", result);
        String value = tempTable.get("key");
        assertTrue("Not exists", value.equals("value"));
    }
    
    @Test
    public void putKeyExists() {
        tempTable.put("good", "night");
        String last = tempTable.put("good", "day");
        assertTrue("Result should be \"night\"", last.equals("night"));
    }
    
    @Test
    public void removeKeyNotExists() {
        assertNull("Result should be null", tempTable.remove("notExists"));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void removeKeyNull() {
        tempTable.remove(null);
    }
    
    @Test
    public void removeCorrectArgs() {
        tempTable.put("key", "value");
        tempTable.remove("key");
        assertNull("Value should be null", tempTable.get("key"));
    }
    
    private String generateString() {
        Random rand = new Random();
        StringBuilder tempString = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            char c = (char)(rand.nextInt((int)(Character.MAX_VALUE)));
            tempString.append(c);
        }
        return tempString.toString();
    }
    
    @Test
    public void sizeTest() {
        Random rand = new Random();
        length = rand.nextInt(100);
        for (int i = 0; i < length; ++i) {
            String key = generateString();
            String value = generateString();
            tempTable.put(key, value);
        }
        assertTrue("Incorrect size", tempTable.size() == length);
    }
    
    @Test
    public void generateTable() {
        Random rand = new Random();
        length = rand.nextInt(20);
        firstKey = generateString();
        firstValue = generateString();
        tempMap.put(firstKey, firstValue);
        tempTable.put(firstKey, firstValue);
        for (int i = 0; i < length; ++i) {
            String key = generateString();
            String value = generateString();
            tempTable.put(key, value);
            tempMap.put(key, value);
        }
    }
    
    @Test
    public void commitTestPutAndRemoveOneKey() {
        generateTable();
        tempMap.remove(firstKey);
        tempTable.remove(firstKey);
        assertTrue("Commit error", tempMap.size() == tempTable.commit());
    }
    
    @Test
    public void commitTestPutOneKeyTwice() {
        generateTable();
        tempMap.put(firstKey, " ");
        tempTable.put(firstKey, " ");
        assertTrue("Commit error", tempMap.size() == tempTable.commit());
    }
    
    @Test
    public void rollbackTestRandom() {
        commitTestPutAndRemoveOneKey();
        generateTable();
        assertTrue("Rollback error", tempTable.rollback() == (length + 1));
    }
    
    @Test
    public void rollbackTestNoChanges() {
        commitTestPutAndRemoveOneKey();
        assertTrue("Rollback error", tempTable.rollback() == 0);
    }
}
