package ru.fizteh.fivt.students.annasavinova.filemap.tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import ru.fizteh.fivt.students.annasavinova.filemap.DBaseProviderFactory;
import ru.fizteh.fivt.students.annasavinova.filemap.DataBaseProvider;

public class LoggingTest {

    @Test
    public void test() throws IOException {
        DBaseProviderFactory f = new DBaseProviderFactory();
        DataBaseProvider p = (DataBaseProvider) f.create("/home/anna/DBDir");
        System.out.println(p.toString());
    }

}
