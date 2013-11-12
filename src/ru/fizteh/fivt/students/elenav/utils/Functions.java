package ru.fizteh.fivt.students.elenav.utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class Functions {
	
	public static void deleteRecursively(File f) throws IOException {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			if (files != null) {
				for (File file : files) {
					deleteRecursively(file);
				}
			}
		}
		if (!f.delete()) {
			throw new IOException("rm: cannot remove '" + f.getName() + "': Unknown error");
		}
	}
	
	public static Class<?> getClassFromString(String type) {
		type = type.trim();
		if (type.equals("int")) {
			return Integer.class;
		} else if (type.equals("long")) {
			return Long.class;
		} else if (type.equals("byte")) {
			return Byte.class;
		} else if (type.equals("float")) {
			return Float.class;
		} else if (type.equals("double")) {
			return Double.class;
		} else if (type.equals("boolean")) {
			return Boolean.class;
		} else if (type.equals("String")) {
			return String.class;
		} else {
			throw new IllegalArgumentException("invalid type of column: "+type);
		}
	}
	
	public static String getStringFromClass(Class<?> type) {
		switch (type.getSimpleName()) {
		case "Integer":
			return "int";
		case "Long":
			return "long";
		case "Byte":
			return "byte";
		case "Float":
			return "float";
		case "Double":
			return "double";
		case "Boolean":
			return "boolean";
		case "String":
			return "String";
		default:
			return null;
		}
	}
	
	public static Object getClass(String value, String type) throws ParseException {
		try {
			switch (type) {
			
			case "Integer": 
				return Integer.parseInt(value);
				
			case "Long": 
				return Long.parseLong(value);
				
			case "Byte":
				return Byte.parseByte(value);
				
			case "Float": 
				return Float.parseFloat(value);
				
			case "Double": 
				return Double.parseDouble(value);
				
			case "Boolean": 
				return Boolean.parseBoolean(value);
				
			case "String": 
				return value;
				
			default: 
				throw new ParseException("parse error", 0);
				
			}
		} catch (NumberFormatException e) {
			throw new ParseException("parse error", 0);
		}
	}
	
}
