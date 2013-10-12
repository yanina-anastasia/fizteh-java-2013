package ru.fizteh.fivt.students.sterzhanovVladislav.shell;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public class Wrapper {
	public static void main (String[] args) {
		try {
			Shell cmdShell = new Shell();
			if (args.length > 1) {
				InputStream cmdStream = createStream(args);
				cmdShell.execCommandStream(cmdStream, false);
			} else {
				cmdShell.execCommandStream(System.in, true);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		System.exit(0);
	}
	
	private static InputStream createStream(String[] args) {
		StringBuilder argline = new StringBuilder();
		for (String arg : args) {
			argline.append(arg).append(" ");
		}
		String cmdLine = argline.toString();
		return new ByteArrayInputStream(cmdLine.getBytes(Charset.defaultCharset()));
	}
}
