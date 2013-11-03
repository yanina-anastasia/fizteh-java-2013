package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import ru.fizteh.fivt.students.mishatkin.filemap.FileMapDatabaseException;
import ru.fizteh.fivt.students.mishatkin.filemap.FileMapReceiver;
import ru.fizteh.fivt.students.mishatkin.filemap.FileMapReceiverProtocol;
import ru.fizteh.fivt.students.mishatkin.shell.TimeToExitException;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Vladimir Mishatkin on 10/26/13
 */
public class MultiFileHashMapTableReceiver implements FileMapReceiverProtocol {
	private static final int TABLE_OWNING_FILES_COUNT = MultiFileHashMap.TABLE_OWNING_DIRECTORIES_COUNT * MultiFileHashMap.TABLE_OWNING_DIRECTORIES_COUNT;

	private WeakReference<MultiFileHashMapReceiver> delegate;

	private String tableName;

	private List<FileMapReceiver> tableFiles =
			new ArrayList<>(TABLE_OWNING_FILES_COUNT);

	public MultiFileHashMapTableReceiver(String tableName) {
		tableFiles = new ArrayList<>(Collections.<FileMapReceiver>nCopies(TABLE_OWNING_FILES_COUNT, null));
		this.tableName = tableName;
		delegate = null;
	}

	public String getName() {
		return tableName;
	}

	public boolean isSet() {
		return !tableName.equals("");
	}

	public void reset() {
		tableFiles =  new ArrayList<>(Collections.<FileMapReceiver>nCopies(TABLE_OWNING_FILES_COUNT, null));
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setDelegate(MultiFileHashMapReceiver delegate) {
		this.delegate = new WeakReference<>(delegate);
	}

	public MultiFileHashMapReceiver getDelegate() {
		return delegate.get();
	}

	private FileMapReceiver tableForKey(String key) throws MultiFileHashMapException {
		int hashCode = key.hashCode();
		int mod = MultiFileHashMap.TABLE_OWNING_DIRECTORIES_COUNT;
		int directoryIndex = Math.abs(hashCode % mod);
		int fileIndex = Math.abs((hashCode / mod) % mod);
		int indexInFilesList = mod * directoryIndex + fileIndex;
		if (tableFiles.get(indexInFilesList) == null) {
			String directoryName = String.valueOf(directoryIndex) + ".dir";
			String fileName = String.valueOf(fileIndex) + ".dat";
			File directory = new File(new File(getDelegate().getDbDirectoryName(), tableName), directoryName);
			if (!directory.exists()) {
				directory.mkdir();
			} else if (!directory.isDirectory()) {
				throw new MultiFileHashMapException(directoryName + ".dir file already exists and it is most certainly not a directory. OK bye.");
			}
			String pseudoFileName = tableName + File.separator + directoryName + File.separator + fileName;
			FileMapReceiver freshDictionaryFile = null;
			try {
				MultiFileHashMapTableReceiverDelegate delegate = getDelegate();
				freshDictionaryFile = new FileMapReceiver(delegate.getDbDirectoryName(), pseudoFileName,
						delegate.isInteractiveMode(), delegate.getOut());
			} catch (FileMapDatabaseException e) {
				throw new MultiFileHashMapException("Cannot create access or create file for key: " + key);
			}
			if (!freshDictionaryFile.doHashCodesConformHash(directoryIndex, fileIndex, MultiFileHashMap.TABLE_OWNING_DIRECTORIES_COUNT)) {
				throw new MultiFileHashMapException("Keys in " + directoryIndex + " directory and " + fileIndex +
						" file contain some extra keys with unacceptable hash values" );
			}
			tableFiles.set(indexInFilesList, freshDictionaryFile);
		}
		return tableFiles.get(indexInFilesList);
	}

	@Override
	public void putCommand(String key, String value) throws MultiFileHashMapException {
		tableForKey(key).putCommand(key, value);
	}

	@Override
	public void getCommand(String key) throws MultiFileHashMapException {
		tableForKey(key).getCommand(key);
	}

	@Override
	public void removeCommand(String key) throws MultiFileHashMapException {
		tableForKey(key).removeCommand(key);
	}

	public void writeFilesOnDrive() throws MultiFileHashMapException {
		for (FileMapReceiver everyFile : tableFiles) {
			try {
				if (everyFile != null) {
					everyFile.exitCommand();
				}
			} catch (TimeToExitException LoLJustKidding) {
			}
		}
		for (int directoryIndex = 0; directoryIndex < MultiFileHashMap.TABLE_OWNING_DIRECTORIES_COUNT; ++directoryIndex) {
			String directoryName = String.valueOf(directoryIndex) + ".dir";
			File directoryFile = new File(new File(getDelegate().getDbDirectoryName(), tableName), directoryName);
			File subFiles[] = directoryFile.listFiles();
			if (directoryFile.exists() && (subFiles == null || subFiles.length == 0)) {
				getDelegate().removeTableSubDirectoryWithIndex(directoryIndex);
			}
		}
	}

}
