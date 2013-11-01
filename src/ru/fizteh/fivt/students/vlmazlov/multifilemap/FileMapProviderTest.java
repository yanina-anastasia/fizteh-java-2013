package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import org.junit.*;

public class FileMapProviderTest {
	private FileMapProvider provider;
	private final String root = System.getProperty("fizteh.db.dir");

	@Before
	public void setUp() {
		try {
			provider = new FileMapProvider(root, false);
		} catch (ValidityCheckFailedException ex) {
			Assert.fail("validity check failed: " + ex.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void creatingNullShouldFail() {
		provider.createTable(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void gettingNullShouldFail() {
		provider.getTable(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void removingNullShouldFail() {
		provider.removeTable(null);
	}

	@Test(expected = IllegalStateException.class)
	public void removingNonExistingTableShouldFail() {
		provider.removeTable("testNonExist");
	}

	@Test
	public void gettingNonExistingTableShouldFail() {
		Assert.assertNull("should be null", provider.getTable("testNonExist"));
	}

	@Test
	public void gettingCreatedTable() {
		provider.createTable("testGet");
		Assert.assertEquals("should be testGet", "testGet", provider.getTable("testGet").getName());
		provider.removeTable("testGet");
	}

	@Test
	public void gettingRemovedTable() {
		provider.createTable("testRemove");
		provider.removeTable("testRemove");
		Assert.assertNull("should be null", provider.getTable("testRemove"));
	}
} 