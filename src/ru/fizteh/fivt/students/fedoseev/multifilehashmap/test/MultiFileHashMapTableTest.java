package ru.fizteh.fivt.students.fedoseev.multifilehashmap.test;

import junit.framework.Assert;
import org.junit.Test;
import ru.fizteh.fivt.students.fedoseev.multifilehashmap.MultiFileHashMapTable;

public class MultiFileHashMapTableTest {
    private MultiFileHashMapTable table;

    public MultiFileHashMapTableTest() {
        table = new MultiFileHashMapTable("test");
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals("test", table.getName());
    }

    @Test
    public void testGet() throws Exception {
        table.put("England", "for English");
        Assert.assertEquals("for English", table.get("England"));

        table.put("Россия", "для русских");
        Assert.assertEquals("для русских", table.get("Россия"));

        table.put("日本", "日本人のための");
        Assert.assertEquals("日本人のための", table.get("日本"));
    }

    @Test
    public void testPut() throws Exception {
        Assert.assertNull(table.put("rhinoceros", "hippopotamus"));
    }

    @Test
    public void testRemove() throws Exception {
        table.put("burrito", "chimichanga");
        Assert.assertEquals("chimichanga", table.remove("burrito"));
    }

    @Test
    public void testSize() throws Exception {
        Assert.assertEquals(0, table.size());
    }

    @Test
    public void testPutSize() throws Exception {
        table.put("damn", "it all");
        Assert.assertEquals(1, table.size());
    }

    @Test
    public void testCommit() throws Exception {
        Assert.assertEquals(0, table.commit());
    }

    @Test
    public void testPutCommit() throws Exception {
        table.put("cymkih, merge me completely", "please");
        Assert.assertEquals(1, table.commit());
    }

    @Test
    public void testRollback() throws Exception {
        Assert.assertEquals(0, table.rollback());
    }

    @Test
    public void testPutRollback() throws Exception {
        table.put("NO", "motherf*cker");
        Assert.assertEquals(1, table.rollback());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() throws Exception {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEmpty() throws Exception {
        table.get("  \t  \n");
    }

    @Test
    public void testGetNotExisting() throws Exception {
        Assert.assertNull(table.get("devastation"));
    }

    @Test
    public void testPutOverwriteGet() throws Exception {
        table.put("Hey! What`s up, dawg?", "Bog off!");
        table.put("Hey! What`s up, dawg?", "Cool, bro!");
        Assert.assertEquals("Cool, bro!", table.get("Hey! What`s up, dawg?"));
    }

    @Test
    public void testRemoveGet() throws Exception {
        table.put("favoloso", "benissimo");
        table.remove("favoloso");
        Assert.assertNull(table.get("favoloso"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws Exception {
        table.put("smth", null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() throws Exception {
        table.put(null, "smth");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyKey() throws Exception {
        table.put("  \t  \n", "smth");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyValue() throws Exception {
        table.put("smth", "  \t  \n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNull() throws Exception {
        table.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmpty() throws Exception {
        table.put("  \t  \n", "  \t  \n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullEmpty() throws Exception {
        table.put(null, "  \t  \n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyNull() throws Exception {
        table.put("  \t  \n", null);
    }

    @Test
    public void testPutOverwrite() throws Exception {
        table.put("Dolphins", "are stupid");
        Assert.assertEquals("are stupid", table.put("Dolphins", "are smart"));
    }

    @Test
    public void testPutCommitGet() throws Exception {
        table.put("qué guay", "fenomenal");
        Assert.assertEquals(1, table.commit());
        Assert.assertEquals("fenomenal", table.get("qué guay"));
    }

    @Test
    public void testPutRollbackGet() throws Exception {
        table.put("qué guay", "fenomenal");
        Assert.assertEquals(1, table.rollback());
        Assert.assertNull(table.get("qué guay"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {
        table.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmpty() throws Exception {
        table.remove("  \t  \n");
    }

    @Test
    public void testRemoveNotExisting() throws Exception {
        Assert.assertNull(table.remove("devastation"));
    }

    @Test
    public void testPutRemoveCommitGet() throws Exception {
        table.put("crap", "awesomeness");
        Assert.assertEquals("awesomeness", table.remove("crap"));
        Assert.assertEquals(0, table.commit());
        Assert.assertNull(table.get("crap"));
    }

    @Test
    public void testPutCommitRemoveRollbackGet() throws Exception {
        table.put("crap", "awesomeness");
        Assert.assertEquals(1, table.commit());
        Assert.assertEquals("awesomeness", table.remove("crap"));
        Assert.assertEquals(1, table.rollback());
        Assert.assertEquals("awesomeness", table.get("crap"));
    }

    @Test
    public void testPutRemoveCommitGetSize() throws Exception {
        table.put("blah", "bang");
        Assert.assertEquals(1, table.size());
        Assert.assertEquals("bang", table.remove("blah"));
        Assert.assertEquals(0, table.commit());
        Assert.assertEquals(0, table.size());
        Assert.assertNull(table.get("blah"));
    }

    @Test
    public void testPutCommitRemoveRollbackGetSize() throws Exception {
        table.put("blah", "bang");
        Assert.assertEquals(1, table.commit());
        Assert.assertEquals(1, table.size());
        Assert.assertEquals("bang", table.remove("blah"));
        Assert.assertEquals(1, table.rollback());
        Assert.assertEquals(1, table.size());
        Assert.assertEquals("bang", table.get("blah"));
    }

    @Test
    public void testPutSizeCommitRollback() throws Exception {
        table.put("楽しんで", "瞬間");
        Assert.assertEquals(1, table.size());

        table.put("真個", "うれしさ");
        Assert.assertEquals(2, table.size());

        table.put("楽しんで", "瞬間");
        Assert.assertEquals(2, table.commit());
        Assert.assertEquals(2, table.size());

        table.remove("楽しんで");
        Assert.assertEquals(1, table.size());

        table.remove("真個");
        Assert.assertEquals(0, table.size());

        Assert.assertEquals(2, table.rollback());
        Assert.assertEquals(0, table.commit());
        Assert.assertEquals(2, table.size());
    }
}
