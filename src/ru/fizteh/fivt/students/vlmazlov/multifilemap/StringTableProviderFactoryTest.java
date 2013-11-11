package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import org.junit.*;

public class StringTableProviderFactoryTest {
	private StringTableProviderFactory factory;

	@Before
	public void setUp() {
		factory = new StringTableProviderFactory();
	}

	@Test(expected = IllegalArgumentException.class)
	public void creatingNullShouldFail() {
		factory.create(null);
	}
} 