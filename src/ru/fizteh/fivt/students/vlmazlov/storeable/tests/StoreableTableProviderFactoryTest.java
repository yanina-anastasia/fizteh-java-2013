package ru.fizteh.fivt.students.vlmazlov.storeable.tests;

import org.junit.*;
import java.io.IOException;

import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableTableProviderFactory;

public class StoreableTableProviderFactoryTest {
	private StoreableTableProviderFactory factory;

	@Before
	public void setUp() {
		factory = new StoreableTableProviderFactory();
	}

	@Test(expected = IllegalArgumentException.class)
	public void creatingNullShouldFail() throws IOException{
		factory.create(null);
	}
} 