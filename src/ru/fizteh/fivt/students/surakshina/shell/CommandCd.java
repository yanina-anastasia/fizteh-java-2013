package ru.fizteh.fivt.students.surakshina.shell;

import java.io.File;
import java.io.IOException;

public class CommandCd extends AbstractCommand {
    public CommandCd(State stateNew) {
        super(stateNew);
        name = "cd";
        numberOfArguments = 1;
    }

    private File unionWithCurrentPath(String curr) {
        File curr1 = new File(curr);
        if (!curr1.isAbsolute()) {
            curr1 = new File(state.getCurrentDirectory(), curr);
            try {
                curr1 = curr1.getCanonicalFile();
            } catch (IOException exception) {
                state.printError(curr1.toString());
            }
        }
        return curr1;
    }

    @Override
    public void executeProcess(String[] input) {
        File currentFile = unionWithCurrentPath(input[1]);
        if (currentFile.exists() && currentFile.isDirectory()) {
            state.setCurrentDirectory(currentFile);
        } else {
            state.printError("cd: '" + input[1] + "': No such file or directory");
        }
    }
}
