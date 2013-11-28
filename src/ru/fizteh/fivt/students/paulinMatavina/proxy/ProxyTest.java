package ru.fizteh.fivt.students.paulinMatavina.proxy;

import java.io.*;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProxyTest {
    MyLoggingProxyFactory factory;
    TestInterface wrapped;
    StringWriter writer;
    
    @Before
    public void init() throws IOException {
        writer = new StringWriter();
        factory = new MyLoggingProxyFactory();
        ClassImplementingInterface implementation = new ClassImplementingInterface();
        wrapped = (TestInterface) factory.wrap(writer, implementation, TestInterface.class);
    }
    
    @Test(expected = Exception.class)
    public void testIntThrowException() throws Exception {
        wrapped.getIntThrowException(12);
    }
    
    @Test
    public void testTakeStringDoNothing() {
        wrapped.takeStringDoNothing("hi!");
    }
    
    @Test
    public void test() throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(null);
        wrapped.getIntFromIterable(list);
    }
    
    @Test(expected = Exception.class)
    public void testJustThrow() throws Exception {
        wrapped.justThrowException();
    }
    
    
    @After
    public void after() throws IOException {
        System.out.println(writer.getBuffer().toString());
        writer.close();
    }
}
