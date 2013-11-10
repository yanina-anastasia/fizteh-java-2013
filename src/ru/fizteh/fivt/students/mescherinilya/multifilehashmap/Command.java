package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

public interface Command {

    public String getName();

    public int getArgsCount();

    public void execute(String[] args) throws Exception;


}
