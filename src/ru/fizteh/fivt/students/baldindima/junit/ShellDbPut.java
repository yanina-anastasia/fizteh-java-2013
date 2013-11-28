package ru.fizteh.fivt.students.baldindima.junit;

import java.text.ParseException;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

public class ShellDbPut extends ShellIsItCommand {
	private Context context;
	public ShellDbPut(Context nContext){
		setName("put");
		setNumberOfArgs(3);
		context = nContext;
	}
	
	public void run(){
		if (context.table == null){
			System.out.println("no table");
			return;
		}
		Storeable storeable;
		try {
			storeable = ((DataBase) context.table).putStoreable(arguments[1], arguments[2]);
			if (storeable == null){
				System.out.println("new");
			} else {
				System.out.println("overwrite");
				System.out.println(context.provider.serialize(context.table, storeable));
			}
		} catch (ParseException e) {
			System.out.println("wrong type");
		}
		
	}

}
