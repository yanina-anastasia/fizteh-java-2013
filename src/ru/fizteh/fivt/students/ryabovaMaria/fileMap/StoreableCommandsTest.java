package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

public class StoreableCommandsTest {    
    Storeable tempStoreable = null;
    
    @Before
    public void initStoreable() throws IOException {
        List<Class<?>> types = new ArrayList();
        types.add(int.class);       //#0
        types.add(boolean.class);   //#1
        types.add(float.class);     //#2
        types.add(Double.class);    //#3
        types.add(byte.class);      //#4
        types.add(Long.class);      //#5
        types.add(String.class);    //#6
        tempStoreable = new StoreableCommands(types);
    }
    
    @Test (expected = IndexOutOfBoundsException.class)
    public void setNegativeIndex() {
        tempStoreable.setColumnAt(-1, "hello");
    }
    
    @Test (expected = IndexOutOfBoundsException.class)
    public void setBigIndex() {
        tempStoreable.setColumnAt(7, "hello");
    }
    
    @Test (expected = ColumnFormatException.class)
    public void setIncorrectType() {
        tempStoreable.setColumnAt(1, 1);
    }
    
    @Test
    public void setNullValue() {
        tempStoreable.setColumnAt(2, null);
    }
    
    @Test
    public void setCorrectValue() {
        tempStoreable.setColumnAt(6, "null");
        tempStoreable.setColumnAt(3, (double) 1);
        tempStoreable.setColumnAt(2, (float) 1);
    }
    
    @Test
    public void getCorrectValue() {
        tempStoreable.setColumnAt(1, null);
        tempStoreable.setColumnAt(3, (double) 1);
        Object value = tempStoreable.getColumnAt(1);
        assertNull("Should be null", value);
        tempStoreable.getDoubleAt(3);
    }
    
    @Test (expected = ColumnFormatException.class)
    public void getIncorrectType() {
        tempStoreable.setColumnAt(2, (float) 1);
        tempStoreable.getDoubleAt(2);
    }
}