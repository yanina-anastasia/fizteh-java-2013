package ru.fizteh.fivt.students.irinapodorozhnaya.storeable.junit;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;


import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.MyStoreable;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.XMLSerializer;

public class XMLSerializerTest {

    
    private Storeable s;
    private List<Class<?>> columnTypes;
    private String desirialized = "<row><col>Hello</col><col>5</col></row>";
    
    @Before
    public void setUp() {
        columnTypes = new ArrayList<>();
        columnTypes.add(String.class);
        columnTypes.add(Integer.class);
        
    }
    
    @Test
    public void correctDeserialize() throws Exception {
        Storeable s = XMLSerializer.deserialize(columnTypes, desirialized);
        Assert.assertEquals("Hello", s.getStringAt(0));
        Assert.assertEquals(new Integer(5), s.getIntAt(1));      
    }
    
    @Test (expected = XMLStreamException.class)
    public void incorrectDeserialize() throws Exception {
        XMLSerializer.deserialize(columnTypes, "<row>fvdfv");
    }
    
    @Test (expected = ParseException.class)
    public void incorrectTypeDeserialize() throws Exception {
        XMLSerializer.deserialize(columnTypes, "<row><col>Hello</col><col>jghd</col></row>");
    }

    @Test
    public void correctSerialize() throws Exception {
        s = new MyStoreable(columnTypes);
        s.setColumnAt(0, "Hello");
        s.setColumnAt(1, 5);
        String res = XMLSerializer.serialize(columnTypes, s);
        
        Assert.assertEquals(res, desirialized);
    }
    
    @Test (expected = ColumnFormatException.class)    
    public void incorrectSerialize() throws Exception {
        s = new MyStoreable(columnTypes);
        s.setColumnAt(0, "Hello");
        s.setColumnAt(1, 5);
        columnTypes.add(Byte.class);
        XMLSerializer.serialize(columnTypes, s);
    }    
}
