package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.ValueConverter;

import java.text.ParseException;

public class StringValueConverter implements ValueConverter<String> {

    public String convertValueTypeToString(String value) {
        return value;
    }

    public String convertStringToValueType(String value) throws ParseException {
        return value;
    }
}
