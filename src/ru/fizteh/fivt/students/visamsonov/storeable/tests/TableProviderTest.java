package ru.fizteh.fivt.students.visamsonov.storeable.tests;

import org.junit.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.visamsonov.storage.TableDirectory;

public class TableProviderTest {

	private static TableProvider provider;

	@Before
	public void before () throws Exception {
		new File("/tmp/dbtest").mkdirs();
		provider = new TableDirectory("/tmp/dbtest");
	}

	@Test
	public void testCreateGetRemoveTable () throws Exception {
		List<Class<?>> types = new ArrayList<Class<?>>(Arrays.asList(Double.class, String.class));
		Assert.assertNotNull(provider.createTable("test", types));
		Assert.assertNotNull(provider.getTable("test"));
		provider.removeTable("test");
	}

	@Test(expected = IllegalStateException.class)
	public void testRemoveTableNonexisting () throws Exception {
		provider.removeTable("blablabla");
	}

	@Test
	public void testGetTableNonexisting () throws Exception {
		Assert.assertNull(provider.getTable("blablabla"));
	}

	@Test
	public void testDeserialize () throws Exception {
		List<Class<?>> types = new ArrayList<Class<?>>(Arrays.asList(Integer.class, String.class));
		provider.createTable("test", types);
		Storeable struct = provider.deserialize(provider.getTable("test"), "<row><col>-1</col><col>aba</col></row>");
		Assert.assertEquals((int) struct.getIntAt(0), -1);
		Assert.assertEquals(struct.getStringAt(1), "aba");
		provider.removeTable("test");
	}

	@Test
	public void testSerialize () throws Exception {
		List<Class<?>> types = new ArrayList<Class<?>>(Arrays.asList(Integer.class, String.class));
		provider.createTable("test", types);
		Storeable struct = provider.createFor(provider.getTable("test"));
		struct.setColumnAt(0, -1);
		struct.setColumnAt(1, "aba");
		Assert.assertEquals(provider.serialize(provider.getTable("test"), struct), "<row><col>-1</col><col>aba</col></row>");
		provider.removeTable("test");
	}

	@Test
	public void testCreateFor () throws Exception {
		List<Class<?>> types = new ArrayList<Class<?>>(Arrays.asList(Integer.class, String.class));
		provider.createTable("test", types);
		Storeable struct = provider.createFor(provider.getTable("test"));
		Assert.assertNull(struct.getColumnAt(0));
		Assert.assertNull(struct.getColumnAt(1));
		provider.removeTable("test");
	}

	@Test
	public void testCreateForVals () throws Exception {
		List<Class<?>> types = new ArrayList<Class<?>>(Arrays.asList(Integer.class, String.class));
		provider.createTable("test", types);
		List<?> values = new ArrayList<Object>(Arrays.asList(-1, "aba"));
		Storeable struct = provider.createFor(provider.getTable("test"), values);
		Assert.assertEquals((int) struct.getIntAt(0), -1);
		Assert.assertEquals(struct.getStringAt(1), "aba");
		provider.removeTable("test");
	}
}