package ru.fizteh.fivt.students.irinapodorozhnaya.utils;

import java.io.IOException;
import java.util.Scanner;

public class Mode {
	 private Mode() {}
	 public static void batchMode(String[] args, State st) {
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

	 private static void parseAndExecute(String arg, State st) throws IOException {
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

	 public static void interactiveMode(State st) {
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
