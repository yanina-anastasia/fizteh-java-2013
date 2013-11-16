package ru.fizteh.fivt.students.inaumov.filemap.builders;

import java.io.File;
import java.util.Set;

public interface TableBuilder {
    public String get(String key);

    public void put(String key, String value);

    public Set<String> getKeys();

    public File getTableDir();

    public void setCurrentFile(File currentFile);
}
