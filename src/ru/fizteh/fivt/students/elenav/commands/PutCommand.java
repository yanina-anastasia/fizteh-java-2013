package ru.fizteh.fivt.students.elenav.commands;

import java.text.ParseException;

import javax.xml.stream.XMLStreamException;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.storeable.StoreableTableState;

public class PutCommand extends AbstractCommand {

	public PutCommand(FilesystemState s) {
		super(s, "put", 2);
	}

	public void execute(String[] args) {
		FilesystemState table = getState();
		if (table.getWorkingDirectory() == null) {
			getState().getStream().println("no table");
		} else {
			try {
				if (table.get(args[1]) != null) {
					getState().getStream().println("overwrite");
					getState().getStream().println(table.put(args[1], args[2]));
				}
				else {
					table.put(args[1], args[2]);
					getState().getStream().println("new");
				}
			} catch (ParseException | XMLStreamException e) {
				System.err.println("invalid input: " + e.getMessage()+" $"+
							StoreableTableState.class.cast(table).getColumnsCount());
			}
		}
	}	
}
