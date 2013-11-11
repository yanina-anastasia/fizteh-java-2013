package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

public interface Command {

    public abstract String getName();

    public abstract int getArgsCount();

    public abstract void execute(String[] args) throws Exception;


}
