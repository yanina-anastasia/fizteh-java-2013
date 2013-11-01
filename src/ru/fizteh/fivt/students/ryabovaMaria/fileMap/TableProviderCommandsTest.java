package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class TableProviderCommandsTest {
    TableProviderFactory tempFactory = new MyTableProviderFactory();
    TableProvider tempTableProvider;
    File createdFolder;
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Before
    public void initTempTableProvider() {
        createdFolder = tempFolder.newFolder("workFolder");
        File existsTable = new File(createdFolder, "table");
        existsTable.mkdir();
        tempTableProvider = tempFactory.create(createdFolder.toString());
        assertNotNull(tempTableProvider);
    }
    
    @After
    public void deleteAll() {
        createdFolder.delete();
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void getTableNameIsNull() {
        tempTableProvider.getTable(null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void getTableNameIsEmpty() {
        tempTableProvider.getTable("");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void  getTableNameIsIncorrect() {
        tempTableProvider.getTable("my\\table.something/wrong");
    }
    
    @Test
    public void getTableNameNotExists() {
        Table tempTable = tempTableProvider.getTable("table13");
        assertNull("Object of Table should be null", tempTable);
    }
    
    @Test
    public void getNameCorrect() {
        Table tempTable = tempTableProvider.getTable("table");
        assertNotNull("Object of Table shouldn't be null", tempTable);
    }
    
    @Test
    public void getOneTableObject() {
        Table tempTableOne = tempTableProvider.getTable("table");
        Table tempTableTwo = tempTableProvider.getTable("table");
        assertTrue("Fail: different objects", tempTableOne == tempTableTwo);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void createTableNameNull() {
        tempTableProvider.createTable(null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void createTableWithIncorrectName() {
        tempTableProvider.createTable("my.new\\incorrect/table");
    }
    
    @Test
    public void createTableWithExistsName() {
        Table tempTable = tempTableProvider.createTable("table");
        assertNull("Object should be null", tempTable);
    }
    
    @Test
    public void createCorrectTable() {
        Table tempTable = tempTableProvider.createTable("table3");
        assertNotNull("Object shouldn't be null", tempTable);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void removeTableWithNullName() {
        tempTableProvider.removeTable(null);
    }
    
    @Test (expected = IllegalStateException.class)
    public void removeTableWithNotExistsName() {
        tempTableProvider.removeTable("table13");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void removeTableWithIncorrectName() {
        tempTableProvider.removeTable("deleteIncorrectName\ntable");
    }
    
    @Test
    public void removeCorrectTable() {
        tempTableProvider.removeTable("table");
    }
}
