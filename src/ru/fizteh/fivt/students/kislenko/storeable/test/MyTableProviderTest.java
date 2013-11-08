package ru.fizteh.fivt.students.kislenko.storeable.test;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.students.kislenko.storeable.MyTableProvider;
import ru.fizteh.fivt.students.kislenko.storeable.MyTableProviderFactory;

import java.io.File;
import java.util.ArrayList;

public class MyTableProviderTest {
    private static MyTableProvider provider;
    private static File databaseDir = new File("database");
    private static ArrayList<Class<?>> typeList = new ArrayList<Class<?>>();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        databaseDir.mkdir();
        provider = factory.create("database");
        typeList.add(Integer.class);
        typeList.add(Integer.class);
        typeList.add(Integer.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyNameTable() throws Exception {
        provider.createTable("..", typeList);
    }
}
