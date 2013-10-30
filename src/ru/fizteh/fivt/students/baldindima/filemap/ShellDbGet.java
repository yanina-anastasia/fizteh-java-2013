package ru.fizteh.fivt.students.baldindima.filemap;

import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

	public class ShellDbGet extends ShellIsItCommand {
		private DataBase dataBase;
		public ShellDbGet(final DataBase dBase){
			dataBase = dBase;
			setName("get");
			setNumberOfArgs(2);
			
		}
		public void run(){
			String value = dataBase.get(arguments[1]);
			if (value == null){
				System.out.println("not found");
			} else {
				System.out.println("found");
				System.out.println(value);
			}
		}

	}



