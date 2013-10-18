package ru.fizteh.fivt.students.visamsonov.storage;

public interface Table {

	String getName();

	String get (String key);

	String put (String key, String value);

	String remove (String key);

	int size();

	int commit();

	int rollback();
}