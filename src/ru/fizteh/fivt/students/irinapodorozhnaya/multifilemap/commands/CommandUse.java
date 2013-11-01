package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands;

import java.io.IOException;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.MultiFileMapState;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;

public class CommandUse extends AbstractCommand{
    private MultiFileMapState state;

    public CommandUse(MultiFileMapState st) {
        super(1);
        state = st;
    }
    
    public String getName() {
        return "use";
    }
    
    public void execute(String[] args) throws IOException {    
        try {
            state.use(args[1]);
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        }
        state.getOutputStream().println("using " + args[1]);        
    }
}
