package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapStoreable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestFileMapStoreable {
    private static FileMapStoreable stIntString;
    @BeforeClass
    public static void setUp() {

        List<Class<?>> list1 = new ArrayList<>();
        list1.add(Integer.class);
        list1.add(String.class);
        stIntString = new FileMapStoreable(list1);

    }

    @Test(expected = IllegalArgumentException.class)
    public void createNull() throws IOException {
        //multiMapFactory.create(null);
    }

    @Test(expected = IOException.class)
    public void createNotExist() throws IOException {
        //multiMapFactory.create("/123123/1243");
    }

    @Test()
    public void createProvider() throws IOException {
        //multiMapFactory.create(pathTables.toString());
    }

}
