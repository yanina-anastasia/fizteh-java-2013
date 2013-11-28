package ru.fizteh.fivt.students.elenarykunova.filemap;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.junit.Test;


public class MyLoggingProxyFactoryTest {

    @Test
    public void test() throws NoSuchMethodException, Throwable {
        MyTableProviderFactory myFactory = new MyTableProviderFactory();
        ArrayList<Object> lala = new ArrayList<Object>();
        ArrayList<Object> la = new ArrayList<Object>();
        la.add(null);
        
        la.add(la);            
        lala.add(la);
        
        Writer writer = new PrintWriter(System.out);            
        MyInvocationHandler handler = new MyInvocationHandler(writer, lala);
        Object[] list = new Object[1];
        list[0] = lala;
        handler.invoke(myFactory, lala.getClass().getDeclaredMethod("add", Object.class), list);
        writer.flush();

    }
}
