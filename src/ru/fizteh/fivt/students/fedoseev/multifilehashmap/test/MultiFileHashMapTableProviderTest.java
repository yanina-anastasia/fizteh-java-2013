package ru.fizteh.fivt.students.fedoseev.multifilehashmap.test;

import junit.framework.Assert;
import org.junit.Test;
import ru.fizteh.fivt.students.fedoseev.multifilehashmap.MultiFileHashMapTable;
import ru.fizteh.fivt.students.fedoseev.multifilehashmap.MultiFileHashMapTableProvider;
import ru.fizteh.fivt.students.fedoseev.multifilehashmap.MultiFileHashMapTableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapTableProviderTest {
    private static File dbDir;
    private static MultiFileHashMapTableProvider tp;

    public MultiFileHashMapTableProviderTest() throws IOException {
        dbDir = new File("test");

        dbDir.mkdirs();

        tp = new MultiFileHashMapTableProviderFactory().create(dbDir.getCanonicalFile().toString());
    }

    @Test
    public void testGetTable() throws Exception {
        MultiFileHashMapTable table1 = tp.createTable("true stories");
        Assert.assertEquals(table1, tp.getTable("true stories"));

        MultiFileHashMapTable table2 = tp.createTable("great expectations");
        Assert.assertEquals(table2, tp.getTable("great expectations"));

        Assert.assertEquals(table1, tp.getTable("true stories"));
        Assert.assertEquals(table2, tp.getTable("great expectations"));
    }

    @Test
    public void testCreateTable() throws Exception {
        Assert.assertNotNull(tp.createTable("lower"));
    }

    @Test
    public void testRemoveTable() throws Exception {
        tp.createTable("madmen");
        tp.createTable("life is pain");
        tp.createTable("loony");

        tp.removeTable("madmen");
        Assert.assertNull(tp.getTable("madmen"));

        tp.removeTable("loony");
        Assert.assertNull(tp.getTable("loony"));

        Assert.assertNotNull(tp.getTable("life is pain"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullTable() throws Exception {
        tp.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEmptyNameTable() throws Exception {
        tp.getTable("   \t   \n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIncorrectNameTable() throws Exception {
        tp.getTable("#$!U#JADE_UFremoval of the brainJHQ#R!342");
    }

    @Test
    public void testGetNotExistingNameTable() throws Exception {
        Assert.assertNull(tp.getTable("be happy"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullTable() throws Exception {
        tp.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyNameTable() throws Exception {
        tp.getTable("   \t   \n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateIncorrectNameTable() throws Exception {
        tp.getTable("!@#$%^%&iesocremoval of the brainImq0114    qcxF!#EDЖД.бБЖДФБ");
    }

    @Test
    public void testCreateExistingNameTable() throws Exception {
        tp.createTable("shake it out");
        Assert.assertNull(tp.createTable("shake it out"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullTable() throws Exception {
        tp.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmptyNameTable() throws Exception {
        tp.removeTable("   \t   \n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveIncorrectNameTable() throws Exception {
        tp.removeTable("w3waezfs3423!@!@e3qC2removal of the brainZАЫ СЯАЫАА!#?<>m^^");
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistingNameTable() throws Exception {
        tp.getTable("do not worry");
    }
}
