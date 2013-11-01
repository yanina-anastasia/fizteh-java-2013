package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands;

import java.io.IOException;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.MultiFileMapState;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;

public class CommandCreate extends AbstractCommand{
    private MultiFileMapState state;
    
    public CommandCreate(MultiFileMapState st) {
        super(1);
        state = st;
    }
    
    public String getName() {
        return "create";
    }
    
    public void execute(String[] args) throws IOException {
            try {
                state.create(args[1]);
        } catch (IllegalStateException e) {
            throw new IOException(e.getMessage());
        }
        state.getOutputStream().println("created");
    }
}
