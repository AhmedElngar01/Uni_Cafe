package com.example.cafe.repository;

import java.io.*;
import java.util.*;

import com.example.cafe.dataSerializer.IDataSerializer;

public class FileRepository<T> implements IRepository<T> {
    private final String fileName;
    private final IDataSerializer<T> serializer;

    public FileRepository(String fileName, IDataSerializer<T> serializer) {
        this.fileName = fileName;
        this.serializer = serializer;
    }

    @Override
    public void save(T obj) {
        List<T> all = getAll();
        String id = serializer.getId(obj);
        all.removeIf(o -> serializer.getId(o).equals(id));
        all.add(obj);
        writeAll(all);
    }

    @Override
    public T getById(String id) {
        for (T obj : getAll()) {
            if (serializer.getId(obj).equals(id)) {
                return obj;
            }
        }
        return null;
    }

    @Override
    public List<T> getAll() {
        List<T> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                T obj = serializer.deserialize(line);
                if (obj != null)
                    list.add(obj);
            }
        } catch (IOException e) {

        }
        return list;
    }

    @Override
    public void delete(String id) {
        List<T> all = getAll();
        all.removeIf(o -> serializer.getId(o).equals(id));
        writeAll(all);
    }

    private void writeAll(List<T> list) {
        try {

            File file = new File(fileName);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (T obj : list) {
                    writer.write(serializer.serialize(obj));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
        }
    }
}
