package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class TableProviderCommandsTest {
    File dir = new File("C:\\Users\\Маша\\my space\\javaWork");
    TableProvider tempTableProvider = new TableProviderCommands(dir);
    
    @Test (expected = IllegalArgumentException.class)
    public void getNameIsNull() {
        tempTableProvider.getTable(null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void getNameIsEmpty() {
        tempTableProvider.getTable("");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void  getNameIsIncorrect() {
        tempTableProvider.getTable("my\\table.something/wrong");
    }
    
    @Test
    public void getNameNotExists() {
        Table tempTable = tempTableProvider.getTable("table13");
        assertTrue("Object of Table should be null", tempTable == null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void getNameIncorrectTable() {
        tempTableProvider.getTable("table1");
    }
    
    @Test
    public void getNameCorrect() {
        Table tempTable = tempTableProvider.getTable("table2");
        assertFalse("Object of Table shouldn't be null", tempTable == null);
    }
    
    @Test
    public void getOneTableObject() {
        Table tempTableOne = tempTableProvider.getTable("table2");
        Table tempTableTwo = tempTableProvider.getTable("table2");
        assertTrue("Fail: different objects", tempTableOne == tempTableTwo);
    }
    
    @Test
    public void createNameExists() {
        Table tempTable = tempTableProvider.createTable("table2");
        assertTrue("Object should be null", tempTable == null);
    }
    
    @Test
    public void createNameNormal() {
        Table tempTable = tempTableProvider.createTable("table3");
        assertFalse("Object shouldn't be null", tempTable == null);
    }
    
    @Test
    public void removeNameNormal() {
        tempTableProvider.removeTable("table3");
    }
    
    @Test (expected = IllegalStateException.class)
    public void removeNameNotExists() {
        tempTableProvider.removeTable("table13");
    }
}
