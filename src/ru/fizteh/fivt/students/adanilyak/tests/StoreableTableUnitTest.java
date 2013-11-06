package ru.fizteh.fivt.students.adanilyak.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.adanilyak.tools.DeleteDirectory;
import ru.fizteh.fivt.students.adanilyak.tools.WorkWithStoreableDataBase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Alexander
 * Date: 05.11.13
 * Time: 1:42
 */
public class StoreableTableUnitTest {
    StoreableTableProvider tableProvider;
    Table testTableEng;
    Table testTableRus;
    File sandBoxDirectory = new File("/Users/Alexander/Documents/JavaDataBase/Tests");

    @Before
    public void setUpTestObject() throws IOException {
        tableProvider = new StoreableTableProvider(sandBoxDirectory);

        List<Class<?>> typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        List<Class<?>> typesTestListTwo = WorkWithStoreableDataBase.createListOfTypesFromString("int double boolean String");

        testTableEng = tableProvider.createTable("testTable9", typesTestListOne);
        testTableRus = tableProvider.createTable("тестоваяТаблица10", typesTestListTwo);
    }

    @After
    public void tearDownTestObject() throws IOException {
        tableProvider.removeTable("testTable9");
        tableProvider.removeTable("тестоваяТаблица10");
        DeleteDirectory.rm(sandBoxDirectory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putKeyWithWhitespaces1Test() {
        List<Object> valuesToPut = new ArrayList<>();
        valuesToPut.add(1);
        valuesToPut.add(2);
        valuesToPut.add(3);
        Storeable needToPut = tableProvider.createFor(tableProvider.getTable("testTable9"), valuesToPut);
        testTableEng.put("key with whitespaces", needToPut);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putKeyWithWhitespaces2Test() {
        List<Object> valuesToPut = new ArrayList<>();
        valuesToPut.add(1);
        valuesToPut.add(2);
        valuesToPut.add(3);
        Storeable needToPut = tableProvider.createFor(tableProvider.getTable("testTable9"), valuesToPut);
        testTableEng.put(" key ", needToPut);
    }
}
