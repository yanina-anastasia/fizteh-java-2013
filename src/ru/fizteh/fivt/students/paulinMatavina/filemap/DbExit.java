package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.io.IOException;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class DbExit implements Command {
    @Override
    public int execute(String[] args, State state) {
        try {
            ((DbState) state).commit();
        } catch (IOException e) {
            System.out.println("filemap: error while writing data to the disk");
            return 1;
        }
        System.exit(0);
        return 0;
    }
    
    @Override
    public String getName() {
        return "exit";
    }
    
    @Override
    public int getArgNum() {
        return 0;
    }   
    
    @Override
    public boolean spaceAllowed() {
        return false;
    }
}
