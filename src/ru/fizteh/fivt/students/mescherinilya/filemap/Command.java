package ru.fizteh.fivt.students.mescherinilya.filemap;

public interface Command {

    public String getName();

    public int getArgsCount();

    public boolean execute(String[] args);


}
