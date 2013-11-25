package ru.fizteh.fivt.students.valentinbarishev.proxy.test;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.valentinbarishev.filemap.MyTableProviderFactory;
import ru.fizteh.fivt.students.valentinbarishev.proxy.MyLoggingProxyFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InvocationHandlerTest {
    static Table table;
    static TableProviderFactory factory;
    static TableProvider provider;
    static List<Class<?>> types;
    static MyLoggingProxyFactory logFactory;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();


    @BeforeClass
    public static void beforeClass() {
        factory = new MyTableProviderFactory();
        logFactory = new MyLoggingProxyFactory();
    }

    @Test
    public void testFactory() throws IOException {
        Writer writer = new FileWriter(folder.newFile("file"));
        TableProviderFactory proxyFactory = (TableProviderFactory) logFactory.wrap(writer,
                factory, TableProviderFactory.class);

        provider = proxyFactory.create(folder.newFolder("folder").getCanonicalPath());

        types = new ArrayList<>();
        types.add(String.class);
        types.add(Integer.class);

        table = provider.createTable("simple", types);


        writer.flush();
        writer.close();

        FileReader reader = new FileReader(new File(folder.getRoot(), "file"));
        char[] buf = new char[10000];
        reader.read(buf);
        System.out.println(buf);
    }

    @Test
    public void testProvider() throws IOException {
        Writer writer = new FileWriter(folder.newFile("file"));
        TableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(folder.newFolder("folder").getCanonicalPath());

        TableProvider proxyProvider = (TableProvider) logFactory.wrap(writer, provider, TableProvider.class);

        types = new ArrayList<>();
        types.add(String.class);
        types.add(Integer.class);

        table = proxyProvider.createTable("simple", types);
        table = proxyProvider.getTable("simple");

        proxyProvider.createFor(table);

        writer.flush();
        writer.close();

        FileReader reader = new FileReader(new File(folder.getRoot(), "file"));
        char[] buf = new char[20000];
        reader.read(buf);
        System.out.println(buf);
    }

    @Test
    public void testProviderError() throws IOException {
        Writer writer = new FileWriter(folder.newFile("file"));
        TableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(folder.newFolder("folder").getCanonicalPath());

        TableProvider proxyProvider = (TableProvider) logFactory.wrap(writer, provider, TableProvider.class);

        types = new ArrayList<>();
        types.add(String.class);
        types.add(Integer.class);

        try {
            table = proxyProvider.createTable("", types);
        } catch (IllegalArgumentException e) {

        }
        writer.flush();
        writer.close();

        FileReader reader = new FileReader(new File(folder.getRoot(), "file"));
        char[] buf = new char[20000];
        reader.read(buf);
        System.out.println(buf);
    }


    @Test
    public void testTestInterface() throws IOException {
        Writer writer = new FileWriter(folder.newFile("file"));
        List<Object> list = new ArrayList<>();
        list.add(list);
        list.add(new Integer(123));
        TestInterface test = (TestInterface) logFactory.wrap(writer, new TestClass(), TestInterface.class);
        test.arrayParameter(list);
        writer.flush();
        writer.close();

        FileReader reader = new FileReader(new File(folder.getRoot(), "file"));
        char[] buf = new char[20000];
        reader.read(buf);
        System.out.println(buf);
    }


}
