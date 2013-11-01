package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.MultiFileMapState;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;

public class CommandRollBack extends AbstractCommand {
    private final MultiFileMapState state;
    
    public CommandRollBack(MultiFileMapState state) {
        super(0);
        this.state = state;
    }
    
    public String getName() {
        return "rollback";
    }
    
    public void execute(String[] args) throws IOException {
        System.out.println(state.rollBack());
    } 
}

