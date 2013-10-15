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
	 		st.checkAndExecute(args);
	 	}
	 }

	 public static void interactiveMode(State st) {
		 Scanner sc = new Scanner(st.getInputStream());
		 do {
			 try {
				 st.getOutputStream().print("$ ");
				 String s = sc.nextLine();
				 parseAndExecute(s, st);
			 } catch (IOException e) {
				 System.err.println(e.getMessage());
			 }	 
		 } while (!Thread.interrupted());
		 sc.close();
	 }
}
