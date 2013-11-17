package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

public interface Command {

    abstract String getName();

    abstract int getArgsCount();

    abstract void execute(String[] args) throws Exception;


}
