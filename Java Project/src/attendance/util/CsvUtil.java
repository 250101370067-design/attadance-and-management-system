package attendance.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {
    public static List<String[]> readCsv(String path) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                rows.add(parseLine(line));
            }
        }
        return rows;
    }

    public static void writeCsv(String path, String[] header, List<String[]> rows) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            if (header != null && header.length > 0) {
                bw.write(escapeAndJoin(header));
                bw.newLine();
            }
            for (String[] row : rows) {
                bw.write(escapeAndJoin(row));
                bw.newLine();
            }
        }
    }

    private static String[] parseLine(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuote = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuote) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++;
                    } else {
                        inQuote = false;
                    }
                } else {
                    cur.append(c);
                }
            } else {
                if (c == ',') {
                    out.add(cur.toString());
                    cur.setLength(0);
                } else if (c == '"') {
                    inQuote = true;
                } else {
                    cur.append(c);
                }
            }
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }

    private static String escapeAndJoin(String[] row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(escape(row[i] == null ? "" : row[i]));
        }
        return sb.toString();
    }

    private static String escape(String value) {
        boolean needQuotes = value.contains(",") || value.contains("\n") || value.contains("\r") || value.contains("\"");
        String v = value.replace("\"", "\"\"");
        return needQuotes ? "\"" + v + "\"" : v;
    }
}


