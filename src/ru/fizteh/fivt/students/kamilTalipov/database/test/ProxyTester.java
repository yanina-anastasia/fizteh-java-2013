package ru.fizteh.fivt.students.kamilTalipov.database.test;

import org.junit.*;

import ru.fizteh.fivt.students.kamilTalipov.database.proxy.XMLProxyLogger;
import ru.fizteh.fivt.students.kamilTalipov.database.proxy.XMLProxyLoggerFactory;

import java.io.PrintWriter;

public class ProxyTester {
    public XMLProxyLoggerFactory loggerFactory;

    @Before
    public void before() {
        loggerFactory = new XMLProxyLoggerFactory();
    }

    @Test
    public void normalCreateTest() {

    }
}
