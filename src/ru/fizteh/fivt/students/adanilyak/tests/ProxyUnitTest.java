package ru.fizteh.fivt.students.adanilyak.tests;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.adanilyak.proxy.ProxyLoggingFactoryImplementation;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableRow;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTable;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProviderFactory;
import ru.fizteh.fivt.students.adanilyak.tools.DeleteDirectory;
import ru.fizteh.fivt.students.adanilyak.tools.StringCreationTools;
import ru.fizteh.fivt.students.adanilyak.tools.WorkWithStoreableDataBase;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.text.ParseException;
import java.util.List;

/**
 * User: Alexander
 * Date: 29.11.13
 * Time: 4:42
 */
public class ProxyUnitTest {
    Proxy testProxy;
    ProxyLoggingFactoryImplementation testProxyLoggingFactory;
    StringWriter writer;
    String sandBoxDirectory = "/Users/Alexander/Documents/JavaDataBase/SandBox";
    List<Class<?>> typesTestList;

    StoreableTableProviderFactory testFactory;
    StoreableTableProvider testProvider;
    StoreableTable testTable;
    StoreableRow testRow;

    @Before
    public void setUpTestObject() throws IOException, ParseException {
        writer = new StringWriter();
        testProxyLoggingFactory = new ProxyLoggingFactoryImplementation();

        File sandBoxFile = new File(sandBoxDirectory);
        sandBoxFile.mkdir();

        testFactory = new StoreableTableProviderFactory();
        testProvider = (StoreableTableProvider) testFactory.create(sandBoxDirectory);

        typesTestList = WorkWithStoreableDataBase.createListOfTypesFromString("int boolean String");

        testTable = (StoreableTable) testProvider.createTable("testTable", typesTestList);
        testRow = (StoreableRow) testProvider.deserialize(testTable, "[0,true,\"Hello World\"]");
    }

    @After
    public void tearDownTestObject() throws IOException {
        DeleteDirectory.rm(new File(sandBoxDirectory));
    }

    /**
     * TEST BLOCK
     * STOREABLE ROW INTERFACE TESTS
     */

    @Test
    public void setColumnAtProxytest() {
        Storeable testProxy = (Storeable) testProxyLoggingFactory.wrap(writer, testRow, Storeable.class);
        testProxy.setColumnAt(0, 57);
        Assert.assertEquals("StoreableRow[57,true,Hello World]", testProxy.toString());
        Assert.assertEquals("<invoke class=\"ru.fizteh.fivt.students.adanilyak.storeable.StoreableRow\" name=\"setColumnAt\"><arguments><argument>0</argument><argument>57</argument></arguments></invoke>", StringCreationTools.cutTimeStamp(writer.toString()));
    }
}
