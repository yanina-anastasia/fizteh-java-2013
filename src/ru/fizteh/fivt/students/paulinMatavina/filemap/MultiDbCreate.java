package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;
import ru.fizteh.fivt.storage.structured.Table;

public class MultiDbCreate implements Command {
    @Override
    public int execute(String[] args, State state) {
        StringTokenizer tokens = new StringTokenizer(args[0]);
        String name = tokens.nextToken();
        
        MyTableProvider multiState = (MyTableProvider) state;
        Table table = null;

        try {
            ArrayList<Class<?>> signature = multiState.parseSignature(args[0], tokens);
            table = multiState.createTable(name, signature);
        } catch (DbWrongTypeException e) {
            System.out.println("wrong type (" + e.getMessage() + ")");
            return 0;
        } catch (IOException e) {
            System.err.println("create: " + e.getMessage());
            return 1;
        }
        
        if (table == null) {
            System.out.println(name + " exists");
        }  else {
            System.out.println("created");
        }
        return 0;
    }
    
    @Override
    public String getName() {
        return "create";
    }
    
    @Override
    public int getArgNum() {
        return 1;
    }   
    
    @Override
    public boolean spaceAllowed() {
        return true;
    }
}
