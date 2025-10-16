package util;

import java.io.*;
import java.util.*;

public class CsvUtil {
    public static List<String[]> leerCsv(String path) throws IOException {
        List<String[]> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                out.add(line.split(","));
            }
        }
        return out;
    }
}