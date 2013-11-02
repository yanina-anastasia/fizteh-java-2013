package ru.fizteh.fivt.students.dubovpavel.strings;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.DispatcherMultiFileHashMap;

import java.io.File;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;

public class TableTest {
    private WrappedMindfulDataBaseMultiFileHashMap db;
    private DispatcherMultiFileHashMap dispatcher;
    private File path;

    private void cleanSandBox() {
        if(path.exists()) {
            path.delete();
        }
    }

    @Before
    public void setUp() {
        String homeDir = System.getProperty("user.home");
        path = new File(homeDir, "sandbox/strings");
        cleanSandBox();
        path.mkdirs();
        dispatcher = createNiceMock(DispatcherMultiFileHashMap.class);
        db = new WrappedMindfulDataBaseMultiFileHashMap(path, dispatcher);
    }

    @After
    public void tearDown() {
        cleanSandBox();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetNull() {
        db.get(null);
    }

    @Test
    public void testPutGet() {
        db.put("key", "value");
        Assert.assertEquals("value", db.get("key"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPutNullValue() {
        db.put("key", null);
    }

    @Test
    public void testRemove() {
        db.put("key", "value");
        Assert.assertEquals("value", db.remove("key"));
        Assert.assertEquals(null, db.get("key"));
    }

    @Test
    public void testCommit() {
        db.put("key", "value");
        db.put("dummy", "stuff");
        db.remove("dummy");
        Assert.assertEquals(db.commit(), 1);
        Assert.assertEquals(db.get("dummy"), null);
        Assert.assertEquals(db.get("key"), "value");
    }

    @Test
    public void testGetName() {
        Assert.assertEquals("strings", db.getName());
    }

    @Test
    public void testRollback() {
        db.put("key", "value");
        Assert.assertEquals(db.rollback(), 1);
        Assert.assertEquals(db.get("key"), null);
    }

    @Test
    public void testCommitRollbackEmpty() {
        Assert.assertEquals(db.commit(), 0);
        Assert.assertEquals(db.rollback(), 0);
    }
}
