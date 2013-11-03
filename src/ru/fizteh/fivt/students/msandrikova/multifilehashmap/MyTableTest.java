package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class MyTableTest {
	private MyTable table;
	private File path;
	
	@After
	public void clear() {
		if(path.exists()) {
			try {
				Utils.remover(path, "test", false);
			} catch (Exception e) {
				System.err.println("Can not remove something");
			}
		}
	}
	
	@Before
	public void setUp() throws Exception {
		path = new File(System.getProperty("user.home"), "sandbox");
		clear();
		table = new MyTable(path, "tableName");
	}

	@Test
	public void testGetName() {
		assertEquals(table.getName(), "tableName");
	}

	@Test
	public void testGet() {
		
	}

	@Test
	public void testPut() {
		
	}

	@Test
	public void testRemove() {
		
	}

	@Test
	public void testSize() {
		
	}

	@Test
	public void testCommit() {
		
	}

	@Test
	public void testRollback() {
		
	}

	@Test
	public void testUnsavedChangesCount() {
		
	}

}
