package ru.fizteh.fivt.students.paulinMatavina.shell;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class Shell {
    public static void main(String[] args) {
        ShellState state = new ShellState();
        state.add(new ShellDir());
        state.add(new ShellCd());
        state.add(new ShellRm());
        state.add(new ShellMove());
        state.add(new ShellPwd());
        state.add(new ShellCopy());
        state.add(new ShellExit());
        state.add(new ShellMkdir());
        
        CommandRunner.run(args, state);
    }
}
