package ru.fizteh.fivt.students.eltyshev.proxy;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.eltyshev.storable.database.DatabaseTableProvider;
import ru.fizteh.fivt.students.eltyshev.storable.database.DatabaseTableProviderFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ProxyMain {
    public static void main(String[] Args) {
        PrintWriter writer = new PrintWriter(System.out);
        TableProviderFactory factory = new DatabaseTableProviderFactory();
        StorageLoggingProxyFactory proxyFactory = new StorageLoggingProxyFactory();
        TableProviderFactory factory1 = (TableProviderFactory) proxyFactory.wrap(writer, factory, DatabaseTableProviderFactory.class);
        try {
            TableProvider provider = factory1.create("ASDSAD");
            TableProvider wrap = (TableProvider) proxyFactory.wrap(writer, provider, DatabaseTableProvider.class);
            wrap.createTable("name", new ArrayList<Class<?>>() {{
                add(Integer.class);
            }});
            wrap.removeTable("name");
            System.out.println(wrap.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writer.flush();
        }
    }
}
