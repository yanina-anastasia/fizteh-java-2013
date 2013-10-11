package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.IOException;
import java.util.Scanner;

public class Shell {
	 public static void main(String[] args) {
		 StateShell st = new StateShell();
		 if (args.length > 0) {
			 batchMode(args, st);
		 } else {
			 interactiveMode(st);
		 }
	 }

	 private static void batchMode(String[] args, StateShell st) {
		 try {
			 parseAndExecute(joinString(args), st);
		 } catch (IOException e) {
			 System.err.println(e.getMessage());
			 System.exit(1);
		 }
	 }

	 public static String joinString(String[] args) {
		 StringBuilder sb = new StringBuilder();
		 for (String s: args) {
			 sb.append(s);
			 sb.append(" ");
		 }
		 return sb.toString();
	 }

	 private static void parseAndExecute(String arg, StateShell st) throws IOException {
		 String[] com = arg.trim().split("\\s*;\\s*");
		 for (String s: com) {
			 String[] args = s.split("\\s+");
			 Command c = st.commands.get(args[0]);
			 if (c != null) {
				 int argsNumber = c.getNumberOfArguments();
				 if (argsNumber > args.length - 1) {
					 throw new IOException(args[0]+ ": Too few arguments");
				 } else if (argsNumber < args.length - 1) {
					 throw new IOException(args[0]+ ": Too many arguments");
				 }
				 c.execute(args);
			 } else {
				 throw new IOException(args[0] + ": No such command");
			 }
		 }
	 }

	 private static void interactiveMode(StateShell st) {
		 Scanner sc = new Scanner(st.in);
		 do {
			 try {
				 st.out.print("$ ");
				 String s = sc.nextLine();
				 parseAndExecute(s, st);
			 } catch (IOException e) {
				 System.err.println(e.getMessage());
			 }	 
		 } while (!Thread.interrupted());
		 sc.close();
	 }
}