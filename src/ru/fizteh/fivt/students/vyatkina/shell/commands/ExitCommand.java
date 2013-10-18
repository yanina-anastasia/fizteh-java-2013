package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.shell.Command;

public class ExitCommand implements Command
{
    @Override
    public void execute (String[] args)  {
        System.exit (0);
    }

    @Override
    public String getName () {
        return "exit";
    }

    @Override
    public int getArgumentCount () {
        return 0;
    }
}
