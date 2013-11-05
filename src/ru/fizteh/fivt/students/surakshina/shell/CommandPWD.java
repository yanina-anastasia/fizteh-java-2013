package ru.fizteh.fivt.students.surakshina.shell;

public class CommandPWD extends AbstractCommand {
    public CommandPWD(State stateNew) {
        super(stateNew);
        name = "pwd";
        numberOfArguments = 0;
    }

    @Override
    public void executeProcess(String[] input) {
        System.out.println(state.getCurrentDirectory());
    }
}
