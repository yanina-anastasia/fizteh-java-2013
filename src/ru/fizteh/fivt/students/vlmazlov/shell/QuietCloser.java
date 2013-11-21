package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.Closeable;
import java.io.IOException;

public class QuietCloser {
	public static void closeQuietly(Closeable resource) {
		try {
			if (null != resource) {
				resource.close();
			}
		} catch (IOException ex) {
			System.err.println( "cp: Exception during Stream.close()" + ex.getMessage());	
		}	
	}
}