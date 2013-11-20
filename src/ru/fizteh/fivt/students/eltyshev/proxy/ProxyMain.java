package ru.fizteh.fivt.students.eltyshev.proxy;

public class ProxyMain {
    public static void main(String[] Args) {
       /* PrintWriter writer = new PrintWriter(System.out);
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
        }      */
    }
}
