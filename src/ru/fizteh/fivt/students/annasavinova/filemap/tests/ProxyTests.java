package ru.fizteh.fivt.students.annasavinova.filemap.tests;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.Writer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.annasavinova.filemap.DBaseProviderFactory;
import ru.fizteh.fivt.students.annasavinova.filemap.MyProxyHandler;

public class ProxyTests {
    DBaseProviderFactory fact;
    TableProvider prov;
    
    @Rule
    public TemporaryFolder root = new TemporaryFolder();
    
    @Before
    public void init() throws Throwable {
        fact = new DBaseProviderFactory();
        prov = fact.create(root.newFolder().toString());
        
    }
    
    @Test
    public void test() throws NoSuchMethodException, SecurityException, Throwable {
        Writer writer = new PrintWriter(System.out);
        MyProxyHandler handler = new MyProxyHandler(writer, prov);
        String[] str = new String[1];
        str[0] = "lala";
        handler.invoke(prov.getClass(), prov.getClass().getMethod("getTable", String.class), str);
        writer.flush();
        
    }

}
