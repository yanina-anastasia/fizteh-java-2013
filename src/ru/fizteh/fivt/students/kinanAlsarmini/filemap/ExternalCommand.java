package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

abstract class ExternalCommand {
    private String name;
    private int argNumber;

    public ExternalCommand(String name, int argNumber) {
        this.name = name;
        this.argNumber = argNumber;
    }

    public String getName() {
        return name;
    }

    public int getArgNumber() {
        return argNumber;
    }


    public abstract void execute(String[] args, Table table);
}
