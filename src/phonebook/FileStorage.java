package phonebook;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileStorage {

    private final String filePath;

    public FileStorage(String filePath) {
        this.filePath = filePath;
    }

    public List<Contact> load() {
        List<Contact> contacts = new ArrayList<>();

        File file = new File(filePath);
        if (!file.exists()) {
            return contacts;
        }

        try {
            String raw = new String(Files.readAllBytes(file.toPath())).trim();

            if (raw.startsWith("[")) raw = raw.substring(1);
            if (raw.endsWith("]"))   raw = raw.substring(0, raw.length() - 1);
            raw = raw.trim();

            if (raw.isEmpty()) return contacts;

            String[] entries = raw.split("\\},\\s*\\{");
            for (String entry : entries) {
                entry = entry.replace("{", "").replace("}", "").trim();
                Map<String, String> fields = parseFields(entry);

                String id    = fields.getOrDefault("id", "");
                String name  = fields.getOrDefault("name", "");
                String phone = fields.getOrDefault("phone", "");
                String email = fields.getOrDefault("email", "");

                if (!id.isEmpty() && !name.isEmpty()) {
                    contacts.add(new Contact(id, name, phone, email));
                }
            }

        } catch (IOException e) {
            System.out.println("Warning: could not read contacts file - " + e.getMessage());
        }

        return contacts;
    }

    public void save(List<Contact> contacts) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < contacts.size(); i++) {
            Contact c = contacts.get(i);
            sb.append("  {");
            sb.append("\"id\":\"").append(escape(c.getId())).append("\",");
            sb.append("\"name\":\"").append(escape(c.getName())).append("\",");
            sb.append("\"phone\":\"").append(escape(c.getPhone())).append("\",");
            sb.append("\"email\":\"").append(escape(c.getEmail())).append("\"");
            sb.append(i < contacts.size() - 1 ? "},\n" : "}\n");
        }

        sb.append("]");

        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.print(sb.toString());
        } catch (IOException e) {
            System.out.println("Error: could not save contacts - " + e.getMessage());
        }
    }

    private Map<String, String> parseFields(String entry) {
        Map<String, String> map = new LinkedHashMap<>();
        int i = 0;

        while (i < entry.length()) {
            int keyStart = entry.indexOf('"', i);
            if (keyStart == -1) break;
            int keyEnd = entry.indexOf('"', keyStart + 1);
            if (keyEnd == -1) break;
            String key = entry.substring(keyStart + 1, keyEnd);

            int colon = entry.indexOf(':', keyEnd);
            if (colon == -1) break;

            int valStart = entry.indexOf('"', colon);
            if (valStart == -1) break;
            int valEnd = entry.indexOf('"', valStart + 1);
            if (valEnd == -1) break;
            String value = entry.substring(valStart + 1, valEnd);

            map.put(key, unescape(value));
            i = valEnd + 1;
        }

        return map;
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
