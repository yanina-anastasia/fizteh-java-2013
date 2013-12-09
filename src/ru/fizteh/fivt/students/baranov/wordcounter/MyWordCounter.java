package ru.fizteh.fivt.students.baranov.wordcounter;

import ru.fizteh.fivt.file.WordCounter;

import java.io.*;
import java.util.*;

public class MyWordCounter implements WordCounter {
    public void count(List<File> files, OutputStream out, boolean aggregate) throws IOException {
        Map<String, Integer> mapOfWords = new HashMap<String, Integer>();
        if (files.isEmpty()) {
            throw new java.lang.IllegalArgumentException();
        }

        for (int i = 0; i < files.size(); ++i) {
            if (!files.get(i).exists()) {
                if (!aggregate)
                    out.write((files.get(i).getName() + ": file not found\n").getBytes());
                continue;
            }
            if (files.get(i).isHidden()) {
                if (!aggregate)
                    out.write((files.get(i).getName() + ": file not available\n").getBytes());
                continue;
            }
            Scanner scanner = new Scanner(files.get(i));
            while (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                String[] words = Parse(str.trim());

                for (int j = 0; j < words.length; ++j) {
                    if (mapOfWords.get(words[j]) == null) {
                        mapOfWords.put(words[j], 1);
                    } else {
                        mapOfWords.put(words[j], mapOfWords.get(words[j]) + 1);
                    }
                }
            }
            if (!aggregate) {
                out.write((files.get(i).getName() + ":\n").getBytes());
                PrintMap(mapOfWords, out);
                mapOfWords = new HashMap<String, Integer>();
            }
        }
        if (aggregate) {
            PrintMap(mapOfWords, out);
        }
    }

    private void PrintMap(Map<String, Integer> map, OutputStream out) {
        Map<String, Integer> treeMap = new TreeMap<String, Integer>(map);
        for (final Map.Entry<String, Integer> entry : treeMap.entrySet()) {
            try {
                out.write((entry.getKey() + " " + entry.getValue() + "\n").getBytes());
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    private String[] Parse(String s) {
        List<String> listOfWords = new ArrayList<String>();
        s = s.toLowerCase();

        String currentString = "";
        for (int i = 0; i < s.length(); ++i) {
            String ch = Character.toString(s.charAt(i));
            if (ch.matches("[0-9a-z]*") || ch.equals("-")) {
                currentString = currentString + ch;
            } else {
                if (!currentString.equals("")) {
                    listOfWords.add(currentString);
                }
                currentString = "";
            }
        }
        if (!currentString.equals("")) {
            listOfWords.add(currentString);
        }

        String[] simpleArray = new String[listOfWords.size()];
        return listOfWords.toArray(simpleArray);
    }
}
