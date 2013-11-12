package ru.fizteh.fivt.students.visamsonov.storage;

import java.io.File;

public class StructuredTableFactory implements StructuredTableProviderFactoryInterface {

	public StructuredTableProviderInterface create (String dir) {
		if (dir == null || dir.trim().isEmpty() || !(new File(dir).isDirectory())) {
			throw new IllegalArgumentException();
		}
		return new StructuredTableDirectory(dir);
	}
}