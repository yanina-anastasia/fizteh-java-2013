package ru.fizteh.fivt.students.annasavinova.filemap;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DBaseProviderFactoryTest {
    DBaseProviderFactory test;

    @Rule
    public TemporaryFolder root = new TemporaryFolder();

    @Before
    public void init() {
        try {
            System.setProperty("fizteh.db.dir", root.newFolder().getAbsolutePath());
            test = new DBaseProviderFactory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() {
        try {
            test.create(null);
        } catch (IOException e) {
            fail("Unexpected IOException");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNotExisting() {
        try {
            test.create(root.newFolder().getAbsolutePath() + "not_existing_name");
        } catch (IOException e) {
            fail("Unexpected IOException");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFile() {
        try {
            test.create(root.newFile().getAbsolutePath());
        } catch (IOException e) {
            fail("Unexpected IOException");
        }
    }

    @Test
    public void testCreate() {
        try {
            assertNotNull(test.create(root.newFolder().getAbsolutePath()));
        } catch (Throwable e) {
            fail("Unexpected exception");
        }

    }
}
