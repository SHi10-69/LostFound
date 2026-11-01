package com.smartlostfound.storage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {

    private static final String DATA_FOLDER = "data";

    static {
        File folder = new File(DATA_FOLDER);

        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static <T> void saveList(
            String fileName,
            List<T> list) throws IOException {

        File file = new File(
                DATA_FOLDER + File.separator + fileName
        );

        try (ObjectOutputStream output =
                     new ObjectOutputStream(
                             new FileOutputStream(file))) {

            output.writeObject(list);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> loadList(
            String fileName) {

        File file = new File(
                DATA_FOLDER + File.separator + fileName
        );

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(
                             new FileInputStream(file))) {

            return (List<T>) input.readObject();

        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<>();
        }
    }
}