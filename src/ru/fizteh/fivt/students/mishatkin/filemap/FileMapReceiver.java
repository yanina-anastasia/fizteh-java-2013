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
			if (!dbFile.exists() || (dbFile.exists() && dbFile.isDirectory())) {
				dbFile.createNewFile();
			}
			in = new FileInputStream(dbFile.getCanonicalFile());
		} catch (IOException e) {
			throw new MissingFileMapDatabaseException("DB file not found.");
		} finally {
			DataInputStream dis = null;
			try {
				dis = new DataInputStream(in);
				boolean hasNext = true;
				while (hasNext) {
					try {
						dis.mark(1024 * 1024); // 1 MB
						int keyLength = dis.readInt();
						int valueLength = dis.readInt();
						byte[] keyBinary = new byte[keyLength];
						byte[] valueBinary = new byte[valueLength];
						dis.read(keyBinary, 0, keyLength);
						dis.read(valueBinary, 0, valueLength);
						String key = new String(keyBinary, "UTF-8");
						String value = new String(valueBinary, "UTF-8");
						dictionary.put(key, value);
					} catch (EOFException e) {
						hasNext = false;
					} catch (NullPointerException | IOException e) {
						throw new MissingFileMapDatabaseException("DB file missing or corrupted.");
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
		String oldValue = dictionary.get(key);
		if (oldValue != null) {
			out.println("overwrite");
			out.println(oldValue);
		} else {
			out.println("new");
		}
		dictionary.put(key, value);
	}

	public void removeCommand(String key) {
		if (dictionary.remove(key) != null){
			out.println("removed");
		} else {
			out.println("not found");
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
				if (dos != null) {
					dos.close();
				}
			} catch (IOException ignored) {
			}
		}
	}
}
