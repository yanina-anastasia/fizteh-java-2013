package ru.fizteh.fivt.students.isItJavaOrSomething.Shell;

import java.io.File;
import java.io.IOException;


public class Cd implements Commands{
    
    public String getCommandName() {
        return "cd";
    }

    public int getArgumentQuantity() {
        return 1;
    }
    
    public void implement(String[] args, Shell.ShellState state) throws SomethingIsWrong {
        String newLocation = args[0]; //path
        File newDisposition = UtilMethods.getAbsoluteName(newLocation, state);//Directory
        if (newDisposition.isDirectory() == false) {
            throw new SomethingIsWrong("cd: " + newLocation + "such directory doesn't exist.");
        }
        try {
            state.changeCurDir(newDisposition.getCanonicalPath());
        } catch (IOException e) {
            throw new SomethingIsWrong(
                    "cd: Error aquired while getting canonical path of new directory message: "
                    + e.getMessage());
        }
    }
}
