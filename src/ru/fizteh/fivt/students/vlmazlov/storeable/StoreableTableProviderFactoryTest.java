package ru.fizteh.fivt.students.vlmazlov.storeable;

import org.junit.*;

public class StoreableTableProviderFactoryTest {
	private StoreableTableProviderFactory factory;

	@Before
	public void setUp() {
		factory = new StoreableTableProviderFactory();
	}

	@Test(expected = IllegalArgumentException.class)
	public void creatingNullShouldFail() {
		factory.create(null);
	}
} 