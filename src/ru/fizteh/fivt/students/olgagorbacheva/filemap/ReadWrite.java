package ru.fizteh.fivt.students.olgagorbacheva.filemap;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import ru.fizteh.fivt.students.olgagorbacheva.shell.State;

public class ReadWrite {
      
      private Storage storage;
      
      public ReadWrite (Storage storage) {
            this.storage = storage;
      }

      public void readFile(State state) throws FileNotFoundException, IOException,
                  FileMapException {
            for (int i = 0; i < 16; i++) {
                  File currentDir = new File(state.getState(),
                              String.valueOf(i) + ".dir");
                  if (currentDir.exists() && currentDir.isDirectory()
                              && currentDir.canRead()) {
                        for (int j = 0; j < 16; j++) {
                              File currentFile = new File(currentDir,
                                          String.valueOf(j) + ".dat");
                              if (currentFile.exists()
                                          && currentFile.isFile()
                                          && currentDir.canRead()) {
                                    read(currentFile);
                              } else {
                                    if (currentFile.exists() && !(currentFile.isFile()
                                                && currentDir.canRead())) {
                                          throw new FileMapException("База данных неподходящего формата");
                                    }
                              }
                        }
                  } else {
                        if (currentDir.exists() && !(currentDir.isDirectory()
                                    && currentDir.canRead())) {
                              throw new FileMapException("База данных неподходящего формата");
                        }                             
                  }
            }
      }
      
      @SuppressWarnings("resource")
      public void read(File dataBaseFile) throws FileNotFoundException, IOException,
                  FileMapException {
            RandomAccessFile reader = new RandomAccessFile(dataBaseFile, "r");
            if (reader.length() == 0) {
                  return;
            }

            ArrayList<Integer> offsets = new ArrayList<Integer>();
            ArrayList<String> keys = new ArrayList<String>();
            ArrayList<String> values = new ArrayList<String>();

            try {
                  do {
                        ArrayList<Byte> keySymbols = new ArrayList<Byte>();
                        byte b = reader.readByte();
                        while (b != 0) {
                              keySymbols.add(b);
                              b = reader.readByte();
                        }
                        byte[] bytes = new byte[keySymbols.size()];
                        for (int i = 0; i < bytes.length; ++i) {
                              bytes[i] = keySymbols.get(i);
                        }
                        keys.add(new String(bytes, "UTF-8"));

                        int offset = reader.readInt();
                        if ((offset <= 0)
                                    || (!offsets.isEmpty() && offset <= offsets
                                                .get(offsets.size() - 1))
                                    || (offset >= reader.length())) {
                              throw new FileMapException("Неверное значение сдвигов");
                        }
                        offsets.add(offset);

                  } while (reader.getFilePointer() != offsets.get(0));
            } catch (EOFException e) {
                  throw new FileMapException("Файл законнчен раньше времени");
            }

            offsets.add((int) reader.length());

            for (int i = 0; i < keys.size(); ++i) {
                  byte[] bytes = new byte[offsets.get(i + 1) - offsets.get(i)];
                  reader.readFully(bytes);
                  values.add(new String(bytes, "UTF-8"));
            }

            for (int i = 0; i < keys.size(); ++i) {
                  if (!storage.put(keys.get(i), values.get(i))) {
                        throw new FileMapException("Значение ключа не уникально");
                  }
            }
            reader.close();
      }
      
      public void writeFile(State state)
                  throws FileNotFoundException, IOException, FileMapException {
            
            if (storage.getSize() == 0) {
                  return;
            }
            
            ArrayList<Map<String, String>> dataBase = new ArrayList<Map<String, String>>();
            Iterator<Map.Entry<String, String>> it = storage.getMap().entrySet().iterator();
            
            for (int i = 0; i < 256; i++) {
                  dataBase.add(new HashMap<String, String>());
            }
            while (it.hasNext()) {
                  Map.Entry<String, String> elem = it.next();
                  int a, b;
                  a = elem.getKey().hashCode() % 16;
                  b = elem.getKey().hashCode() / 16 % 16;
                  dataBase.get(a * 16 + b).put(elem.getKey(), elem.getValue());
            }
            for (int i = 0; i < 256; i++) {
                  if (dataBase.get(i).size() != 0) {
                        File dir = new File(state.getState(), String.valueOf(i / 16) + ".dir");
                        File file;
                        if (dir.exists()) {
                              if (!dir.isDirectory()) {
                                    throw new FileMapException("База данных неподходящего формата");
                              }
                              file = new File(dir, String.valueOf(i % 16) + ".dat");
                              if (file.exists()) {
                                    if (!file.isFile()) {
                                          throw new FileMapException("База данных неподходящего формата");
                                    }
                              } else {
                                    file.createNewFile();
                              }
                        } else {
                              dir.mkdir();
                              file = new File(dir, String.valueOf(i % 16) + ".dat");
                              file.createNewFile();
                        }
                        write(file, dataBase.get(i));
                  }
            }
            storage.clear();
      }

      public void write(File dataBaseFile, Map<String, String> storage)
                  throws FileNotFoundException, IOException {
            RandomAccessFile writer = new RandomAccessFile(dataBaseFile, "rw");
            writer.setLength(0);

            Integer offset = countOffset(storage);
            ArrayList<String> values = new ArrayList<String>();

            Iterator<Map.Entry<String, String>> it = storage
                        .entrySet().iterator();

            while (it.hasNext()) {
                  Map.Entry<String, String> elem = it.next();
                  writer.write(elem.getKey().getBytes("UTF-8"));
                  writer.write("\0".getBytes("UTF-8"));
                  writer.writeInt(offset);
                  offset += elem.getValue().getBytes("UTF-8").length;
                  values.add(elem.getValue());
            }
            for (int i = 0; i < values.size(); i++) {
                  writer.write(values.get(i).getBytes("UTF-8"));
            }

            writer.close();
      }

      private Integer countOffset(Map<String, String> storage) throws IOException {
            Integer curOffset = 0;
            Iterator<Map.Entry<String, String>> it = storage
                        .entrySet().iterator();
            while (it.hasNext()) {
                  curOffset += it.next().getKey().getBytes("UTF-8").length + 1 + 4;
            }

            return curOffset;

      }

}