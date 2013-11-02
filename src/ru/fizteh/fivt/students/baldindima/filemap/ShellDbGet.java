package ru.fizteh.fivt.students.baldindima.filemap;

import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

	public class ShellDbGet extends ShellIsItCommand {
		private DataBaseTable dataBaseTable;
		public ShellDbGet(final DataBaseTable dBaseTable){
			dataBaseTable = dBaseTable;
			setName("get");
			setNumberOfArgs(2);
			
		}
		public void run(){
			if (!dataBaseTable.exists()){
				System.out.println("no table");
				return;
			}
			String value = dataBaseTable.get(arguments[1]);
			if (value == null){
				System.out.println("not found");
			} else {
				System.out.println("found");
				System.out.println(value);
			}
		}

	}



