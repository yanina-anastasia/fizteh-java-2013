package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import org.junit.Test;
import static org.junit.Assert.*;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class MyTableProviderFactoryTest {
    MyTableProviderFactory tempFactory = new MyTableProviderFactory();
    
    @Test (expected = IllegalArgumentException.class)
    public void dirIsEmpty() {
        tempFactory.create("");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void dirIsNull() {
        tempFactory.create(null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void dirNotExists() {
        tempFactory.create("C:\\Users\\Маша\\my space\\notExists");
    }
    
    @Test
    public void dirIsCorrect() {
        TableProvider createdTableProvider = tempFactory.create("C:\\Users\\Маша\\my space\\javaWork");
        assertFalse("Object of TableProvider sholdn't be null", createdTableProvider == null);
    }
}
