package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.storeable;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import static org.junit.Assert.*;

public class StoreableTester {
    public static Storeable row;
    public static List<Class<?>> sampleSignature;
    
    @Before
    public void init() {
        sampleSignature = new ArrayList<Class<?>>();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void initNullShouldFail() {
        sampleSignature.add(String.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, "Test string value");
        row = new StoreableRow(null);
    }

    @Test(expected = ColumnFormatException.class)
    public void putMismatchedValueShouldFail() {
        sampleSignature.add(String.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, "Test string value");
        row.setColumnAt(0, 5);
    }
    
    @Test(expected = ColumnFormatException.class) 
    public void getMismatchedFieldShouldFail() {
        sampleSignature.add(String.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, "Test string value");
        row.getFloatAt(0);
    }
    
    @Test
    public void getValidField() {
        sampleSignature.add(String.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, "Test string value");
        assertEquals(row.getStringAt(0), "Test string value");
    }
    
    @Test
    public void getInt() {
        sampleSignature.add(Integer.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, 5);
        assertEquals(row.getIntAt(0), (Integer) 5);
    }

    @Test
    public void getLong() {
        sampleSignature.add(Long.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, Long.MAX_VALUE);
        assertEquals(row.getLongAt(0), (Long) Long.MAX_VALUE);
    }

    @Test
    public void getByte() {
        sampleSignature.add(Byte.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, Byte.MIN_VALUE);
        assertEquals(row.getByteAt(0), (Byte) Byte.MIN_VALUE);
    }

    @Test
    public void getFloat() {
        sampleSignature.add(Float.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, Float.MIN_VALUE);
        assertEquals(row.getFloatAt(0), (Float) Float.MIN_VALUE);
    }

    @Test
    public void getDouble() {
        sampleSignature.add(Double.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, Double.MAX_VALUE);
        assertEquals(row.getDoubleAt(0), (Double) Double.MAX_VALUE);
    }

    @Test
    public void getBoolean() {
        sampleSignature.add(Boolean.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, true);
        assertEquals(row.getBooleanAt(0), true);
    }

    @Test(expected = ColumnFormatException.class)
    public void getStringShouldFail() {
        sampleSignature.add(Integer.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, 5);
        row.getStringAt(0);
    }
    
    @Test(expected = ColumnFormatException.class)
    public void getIntShouldFail() {
        sampleSignature.add(String.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, "some string");
        row.getIntAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void getLongShouldFail() {
        sampleSignature.add(String.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, "some string");
        row.getLongAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void getByteShouldFail() {
        sampleSignature.add(String.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, "some string");
        row.getByteAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void getFloatShouldFail() {
        sampleSignature.add(String.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, "some string");
        row.getFloatAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void getDoubleShouldFail() {
        sampleSignature.add(String.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, "some string");
        row.getDoubleAt(0);
    }

    @Test(expected = ColumnFormatException.class)
    public void getBooleanShouldFail() {
        sampleSignature.add(String.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, "some string");
        row.getBooleanAt(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getStringShouldFailIOOB() {
        sampleSignature.add(String.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, "some string");
        row.getStringAt(5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIntegerShouldFailIOOB() {
        sampleSignature.add(Integer.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, 5);
        row.getStringAt(5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getLongShouldFailIOOB() {
        sampleSignature.add(Long.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, Long.MAX_VALUE);
        row.getStringAt(5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getByteShouldFailIOOB() {
        sampleSignature.add(Byte.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, Byte.MIN_VALUE);
        row.getStringAt(5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getFloatShouldFailIOOB() {
        sampleSignature.add(Float.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, Float.MAX_VALUE);
        row.getStringAt(5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getDoubleShouldFailIOOB() {
        sampleSignature.add(Double.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, Double.MAX_VALUE);
        row.getStringAt(5);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getBooleanShouldFailIOOB() {
        sampleSignature.add(Boolean.class);
        row = new StoreableRow(sampleSignature);
        row.setColumnAt(0, true);
        row.getStringAt(5);
    }

}
