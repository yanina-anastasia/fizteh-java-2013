package ru.fizteh.fivt.students.visamsonov.tests;

import org.junit.*;
import java.io.File;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.visamsonov.storage.TableFactory;

public class TableProviderFactoryTest {

	private static TableProviderFactory factory;

	@Before
	public void before () throws Exception {
		new File("/tmp/dbtest").mkdirs();
		factory = new TableFactory();
	}

	@Test
	public void testCreate () throws Exception {
		Assert.assertNotNull(factory.create("/tmp/dbtest"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNonexisting () throws Exception {
		factory.create("/nonexistingpathblabla");
	}
}