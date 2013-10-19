package ru.fizteh.fivt.students.kamilTalipov.shell;

public class SimpleCommand implements Command {
    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getNumberOfArguments() {
        return numberOfArguments;
    }

    @Override
    public boolean equalName(String name) {
        return this.name.equals(name);
    }

    @Override
    public boolean equalNumberOfArguments(int numberOfArguments) {
        return this.numberOfArguments == numberOfArguments;
    }

    @Override
    public void run(Shell shell, String[] args) throws IllegalArgumentException {

    }

    protected  SimpleCommand(String name, int numberOfArguments) {
        this.name = name;
        this.numberOfArguments = numberOfArguments;
    }

    protected String name;
    protected int numberOfArguments;
}
