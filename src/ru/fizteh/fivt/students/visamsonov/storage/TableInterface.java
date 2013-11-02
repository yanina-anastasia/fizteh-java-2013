package ru.fizteh.fivt.students.visamsonov.storage;

import ru.fizteh.fivt.storage.strings.Table;

public interface TableInterface extends Table {

	String getName ();

	String get (String key);

	String put (String key, String value);

	String remove (String key);

	int size ();

	int commit ();

	int rollback ();

	/* Этот костыль ради этой функции... FUUUUU!!! */
	int unsavedChanges ();
}