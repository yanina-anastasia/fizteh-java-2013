package ru.fizteh.fivt.students.irinapodorozhnaya.storable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Utils;

public class CommandCreate extends AbstractCommand {
    
    private StorableState state;
    
    public CommandCreate(StorableState state) {
        super(-1);
        this.state = state;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public void execute(String[] args) throws IOException {
        try {
            state.create(args[1], parser(args));
        } catch (IllegalArgumentException e) {
            throw new IOException("create: " + e.getMessage());
        }
        state.getOutputStream().println("created");
    }
    
    public static List<Class<?>> parser(String[] args) throws IOException {
        if (args.length < 3) {
            throw new IOException("create: too few arguments");
        }
        int last = args.length - 1;
        if (!args[2].startsWith("(") || !args[last].endsWith(")")) {
            throw new IOException("create: Input not matchs \"create tablename (type1 ... typeN)\"");
        }
        
        List<Class<?>> columnType = new ArrayList<>();
        
        args[2] = (args[2].length() > 1) ? args[2].substring(1) : null; 
        args[last] = (args[last].length() > 1) ? args[last].substring(0, args[last].length() - 1) : null;
    
        for (int i = 2; i < args.length; ++i) {
            if (args[i] != null) {
               columnType.add(Utils.detectClass(args[i]));
            }
        }    
        return columnType;
    }
}
