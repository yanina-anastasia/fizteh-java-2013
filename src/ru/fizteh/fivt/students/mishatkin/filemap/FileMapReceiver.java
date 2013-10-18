package ru.fizteh.fivt.students.mishatkin.filemap;

import ru.fizteh.fivt.students.mishatkin.shell.*;

import java.io.*;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Vladimir Mishatkin on 10/14/13
 */
public class FileMapReceiver extends ShellReceiver {

	private File dbFile;
	private HashMap<String, String> dictionary = new HashMap<>();

	public FileMapReceiver(String dbDirectory, String dbFileName, boolean interactiveMode, PrintStream out) throws MissingFileMapDatabaseException {
		super(out, interactiveMode);
		FileInputStream in = null;
		try {
			assert dbDirectory != null;
			dbFile = new File(new File( dbDirectory), dbFileName);
			in = new FileInputStream(dbFile.getCanonicalFile());
		} catch (IOException e) {
				throw  new MissingFileMapDatabaseException("DB file not found.");
		} finally {
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
					}catch (NullPointerException e) {
						throw new MissingFileMapDatabaseException("DB file missing or corrupted.");
					} catch (IOException e) {
						hasNext = false;
					}
				}
			} finally {
				try {
					if (dis != null) {
						dis.close();
					}
				} catch (NullPointerException | IOException ignored) {
				}
			}
		}

	}

	public void showPrompt() {
		if (isInteractiveMode()) {
			out.print("$ ");
		}
	}

	public void putCommand(String key, String value) {
		if (dictionary.get(key) != null) {
			out.println("overwrite");
		} else {
			out.println("new");
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
		try {
			writeChangesToFile();
		} catch (ShellException e) {
			System.out.println(e.getMessage());
		}
		super.exitCommand();
	}

	private void writeChangesToFile() throws ShellException {
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(new FileOutputStream(dbFile));
			Set<String> keys = dictionary.keySet();
			for (String key : keys) {
				try {
					String value = dictionary.get(key);
					dos.writeInt(key.getBytes().length);
					dos.writeInt(value.getBytes().length);
					dos.write(key.getBytes());
					dos.write(value.getBytes());
				} catch (IOException e) {
					System.err.println("Internal error.");
				}
			}
		} catch (FileNotFoundException e) {
			throw new ShellException("OK, now someone just took the file out of me, so I cannot even rewrite it.");
		} finally {
			try {
				dos.close();
			} catch (IOException ignored) {
			}
		}
	}
}
