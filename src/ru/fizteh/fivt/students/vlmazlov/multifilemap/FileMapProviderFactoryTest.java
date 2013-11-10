package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import org.junit.*;

public class FileMapProviderFactoryTest {
	private FileMapProviderFactory factory;

	@Before
	public void setUp() {
		factory = new FileMapProviderFactory();
	}

	@Test(expected = IllegalArgumentException.class)
	public void creatingNullShouldFail() {
		factory.create(null);
	}
} 