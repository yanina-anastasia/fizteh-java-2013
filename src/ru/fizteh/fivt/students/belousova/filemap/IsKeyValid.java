package ru.fizteh.fivt.students.belousova.filemap;

import ru.fizteh.fivt.students.belousova.multifilehashmap.Predicate;

public class IsKeyValid implements Predicate<String> {
    @Override
    public boolean apply(String input) {
        return true;
    }
}
