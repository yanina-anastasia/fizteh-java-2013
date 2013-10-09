package ru.fizteh.fivt.students.isItJavaOrSomething.Shell;

import java.io.File;
import java.util.Arrays;
import java.util.List;


public class Dir implements Commands {
    
    public String getCommandName() {
        return "dir";
    }

    public int getArgumentQuantity() {
        return 0;
    }
    
    public void implement(String[] args, Shell.ShellState state) {
        File dir = new File(state.getCurDir());
        String[] files = dir.list();
        List<String> tempObj = Arrays.asList(files);
        String anotherTempObj = UtilMethods.uniteItems(tempObj, " ");
        System.out.println(anotherTempObj);
    }

}
