package ru.fizteh.fivt.students.mishatkin.filemap;

import java.io.*;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Vladimir Mishatkin on 10/14/13
 */
public class FileMapReceiver {

	private boolean interactiveMode;

	private PrintStream out;

	private File dbFile;
	private HashMap<String, String> dictionary = new HashMap<>();

	public boolean isInteractiveMode() {
		return interactiveMode;
	}

	public FileMapReceiver(String dbDirectory, String dbFileName, boolean isInteractiveMode, PrintStream out) throws MissingFileMapDatabaseException {
		this.interactiveMode = isInteractiveMode;
		this.out = out;
		FileInputStream in = null;
		try {
			assert dbDirectory != null;
			dbFile = new File(new File( dbDirectory), dbFileName);
			in = new FileInputStream(dbFile.getCanonicalFile());
		} catch (FileNotFoundException e) {
				throw  new MissingFileMapDatabaseException("DB fle not found.");
		} catch (IOException ignored) {
		}
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(in);
			boolean hasNext = true;
			while (hasNext) {
				try {
					int keyLength = dis.readInt();
					int valueLength = dis.readInt();
					byte[] keyBinary = new byte[keyLength];
					byte[] valueBinary = new byte[valueLength];
					dis.read(keyBinary, 0, keyLength);
					dis.read(valueBinary, 0, valueLength);
					String key = new String(keyBinary, "UTF-8");
					String value = new String(valueBinary, "UTF-8");
					dictionary.put(key, value);
				} catch (IOException e) {
					hasNext = false;
				}
			}
		} finally {
			try {
				dis.close();
			} catch (IOException ignored) {
			}
		}
	}

	public void showPrompt() {
		out.print("$ ");
	}

	public void putCommand(String key, String value) {
		if (dictionary.get(key) != null) {
			out.println("overwrite");
		}
		dictionary.put(key, value);
	}

	public void removeCommand(String key) {
		if (dictionary.remove(key) != null){
			out.println("removed");
		} else {
			out.println("not removed");
		}
	}

	public void getCommand(String key) {
		String value = dictionary.get(key);
		if (value != null) {
			out.println("found");
			out.println(dictionary.get(key));
		} else {
			out.println("not found");
		}
	}

	public void exitCommand() throws TimeToExitException {
		writeChangesToFile();
		throw new TimeToExitException();
	}

	private void writeChangesToFile() {
		DataOutputStream dos = null;
			try {
				dos = new DataOutputStream(new FileOutputStream(dbFile));
				Set<String> keys = dictionary.keySet();
				for (String key : keys) {
					try {
						String value = dictionary.get(key);
						dos.write(toByreArray(key.length()));
						dos.write(toByreArray(value.length()));
						dos.write(key.getBytes());
						dos.write(value.getBytes());
					} catch (IOException e) {
						System.err.println("Internal error.");
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			} finally {
				try {
					dos.close();
				} catch (IOException ignored) {
				}
			}
	}

	private byte[] toByreArray(int value) {
		byte[] retValue = new byte[4];
		retValue[0] = (byte) ((value >> 12) & 15);
		retValue[1] = (byte) ((value >> 8) & 15);
		retValue[2] = (byte) ((value >> 4) & 15);
		retValue[3] = (byte) (value & 15);
		return retValue;
	}
}
