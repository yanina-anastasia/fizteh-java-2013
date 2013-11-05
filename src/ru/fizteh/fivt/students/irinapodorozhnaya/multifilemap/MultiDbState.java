package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.db.DbState;

public interface MultiDbState extends DbState{

    int getCurrentTableSize();

    int rollBack();

    void drop(String name) throws IOException;

    void create(String name) throws IOException;

    void use(String name) throws IOException;

}
