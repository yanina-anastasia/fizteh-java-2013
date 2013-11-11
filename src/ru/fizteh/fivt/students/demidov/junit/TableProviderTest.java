package ru.fizteh.fivt.students.demidov.junit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.Table;

public class TableProviderTest {
	@Before
	public void setUp() {
		try {
			currentProvider = new TableProviderImplementation(System.getProperty("fizteh.db.dir"));
		} catch (IllegalArgumentException catchedException) {
			Assert.fail("unable to create TableProviderImplementation example");
		}
	}
	
	@Test
	public void getTableAfterCreate() {
		Table createdTable = currentProvider.createTable("createdTable");
		Assert.assertEquals("should be createdTable", "createdTable", currentProvider.getTable("createdTable").getName());
		Table table = currentProvider.getTable("createdTable");
		Assert.assertSame("expected the same table as created", createdTable, table);
		table = currentProvider.getTable("createdTable");
		Assert.assertSame("expected the same table as created", createdTable, table);
		currentProvider.removeTable("createdTable");
	}
	
	@Test
	public void getTableAfterRemove() {
		currentProvider.createTable("createdTable");
		currentProvider.removeTable("createdTable");
		Assert.assertNull("expected null", currentProvider.getTable("createdTable"));
	}
	
	//undefined parameters
	@Test(expected = IllegalStateException.class)
	public void removeTableWithNullParamedter() {
		currentProvider.removeTable("ufo");
	}

	@Test
	public void createWithNullParameter() {
		Assert.assertNull("expected null", currentProvider.getTable("ufo"));
	}
	
	//null parameters
	@Test(expected = IllegalArgumentException.class)
	public void createTableWithNullParameter() {
		currentProvider.createTable(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getTableWithNullParameter() {
		currentProvider.getTable(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void removeTableWithNullParameter() {
		currentProvider.removeTable(null);
	}
	
	private TableProviderImplementation currentProvider;
} 