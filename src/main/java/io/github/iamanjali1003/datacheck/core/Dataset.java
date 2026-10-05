package io.github.iamanjali1003.datacheck.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A table of string values, keyed by column name.
 * Values stay as strings; each rule decides how to interpret them.
 */
public final class Dataset {

    private final Map<String, List<String>> columns;

    public Dataset(Map<String, List<String>> columns) {
        this.columns = columns;
    }

    public static Dataset fromCsv(Path file) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String header = reader.readLine();
            if (header == null) {
                throw new IOException("file is empty: " + file);
            }

            String[] names = header.split(",", -1);
            Map<String, List<String>> columns = new LinkedHashMap<>();
            for (String name : names) {
                columns.put(name.trim(), new ArrayList<>());
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] cells = line.split(",", -1);
                if (cells.length != names.length) {
                    throw new IOException("line " + lineNumber + " has " + cells.length
                            + " cells, expected " + names.length);
                }
                for (int i = 0; i < names.length; i++) {
                    columns.get(names[i].trim()).add(cells[i].trim());
                }
            }
            return new Dataset(columns);
        }
    }

    public boolean has(String column) {
        return columns.containsKey(column);
    }

    public List<String> column(String name) {
        List<String> values = columns.get(name);
        if (values == null) {
            throw new IllegalArgumentException("no such column: " + name);
        }
        return values;
    }

    public int rowCount() {
        return columns.isEmpty() ? 0 : columns.values().iterator().next().size();
    }
}
