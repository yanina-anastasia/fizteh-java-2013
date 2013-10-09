package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class DescriptionOfDirectory extends Command {

	public DescriptionOfDirectory() {
		super("dir", 0);
	}
	
	@Override
	public void execute(String[] argumentsList) {
		super.getArgsAcceptor(argumentsList.length - 1);
		if(super.hasError) {
			return;
		}
		
		File[] listOfFiles;
		listOfFiles = Shell.currentDirectory.listFiles();
		
		Arrays.sort(listOfFiles, new Comparator<File>() {
			@Override
			public int compare(File file1, File file2) {
				if(file1.isDirectory()) {
					if(file2.isDirectory()) {
						return file1.compareTo(file2);
					} else {
						return -1;
					}
				} else {
					if(file2.isDirectory()) {
						return 1;
					} else {
						return file1.compareTo(file2);
					}
				}
			}
		});
		for(File fileName : listOfFiles) {
			System.out.println(fileName.getName());
		}
	}

}
