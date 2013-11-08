package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.storeable;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import static org.junit.Assert.*;

public class StoreableTester {
    public static Storeable row;
    
    @Before
    public void init() {
        List<Class<?>> sampleSignature;
        sampleSignature = new ArrayList<Class<?>>();
        sampleSignature.add(String.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, "Test string value");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void initNullShouldFail() {
        row = new StoreableRow(null);
    }

    @Test(expected = ColumnFormatException.class)
    public void putMismatchedValueShouldFail() {
        row.setColumnAt(0, 5);
    }
    
    @Test(expected = ColumnFormatException.class) 
    public void getMismatchedFieldShouldFail() {
        row.getFloatAt(0);
    }
    
    @Test
    public void getValidField() {
        assertEquals(row.getStringAt(0), "Test string value");
    }

}
