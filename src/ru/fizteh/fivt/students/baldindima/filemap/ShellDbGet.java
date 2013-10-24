package ru.fizteh.fivt.students.baldindima.filemap;

	import java.io.IOException;

	import ru.fizteh.fivt.students.baldindima.shell.ShellCommand;

	public class ShellDbGet implements ShellCommand {
		private String name = "get";
		private DataBase dataBase;
		private String[] arguments;
		public ShellDbGet(final DataBase dBase){
			dataBase = dBase;
		}
		public boolean isItCommand(final String[] commands) throws IOException{
			if (commands[0].equals(name)){
			if (commands.length != 2){
				throw new IOException("Invalid number of arguments");
			
			}
			arguments = commands;
			return true;
			}
			return false;
		}
		public void run() throws IOException{
			String value = dataBase.get(arguments[1]);
			if (value == null){
				System.out.println("not found");
			} else {
				System.out.println("found");
				System.out.println(value);
			}
		}

	}



