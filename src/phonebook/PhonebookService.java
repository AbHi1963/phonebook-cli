package phonebook;

import java.util.*;
import java.util.stream.Collectors;

public class PhonebookService {

    private final List<Contact> contacts;
    private final FileStorage storage;
    private int nextId;

    public PhonebookService(FileStorage storage) {
        this.storage = storage;
        this.contacts = storage.load();

        this.nextId = contacts.stream()
            .map(c -> {
                try { return Integer.parseInt(c.getId()); }
                catch (NumberFormatException e) { return 0; }
            })
            .max(Integer::compareTo)
            .orElse(0) + 1;
    }

    public String addContact(String name, String phone, String email) {
        String n = name.trim();
        String p = phone.trim();

        if (n.isEmpty() || p.isEmpty()) {
            return "Name and phone are required.";
        }

        if (!validPhone(p)) {
            return "Phone can only contain digits, spaces, + or hyphens.";
        }

        boolean exists = contacts.stream().anyMatch(c -> c.getPhone().equals(p));
        if (exists) {
            return "That phone number is already saved.";
        }

        String id = String.valueOf(nextId++);
        contacts.add(new Contact(id, n, p, email.trim()));
        storage.save(contacts);

        return "Saved as contact #" + id;
    }

    public String updateContact(String id, String newName, String newPhone, String newEmail) {
        Contact c = findById(id.trim());
        if (c == null) {
            return "No contact with ID " + id;
        }

        if (!newName.isEmpty()) {
            c.setName(newName.trim());
        }

        if (!newPhone.isEmpty()) {
            String p = newPhone.trim();
            if (!validPhone(p)) {
                return "Phone can only contain digits, spaces, + or hyphens.";
            }
            boolean taken = contacts.stream()
                .anyMatch(x -> !x.getId().equals(id.trim()) && x.getPhone().equals(p));
            if (taken) {
                return "That number belongs to another contact.";
            }
            c.setPhone(p);
        }

        if (!newEmail.isEmpty()) {
            c.setEmail(newEmail.trim());
        }

        storage.save(contacts);
        return "Updated.";
    }

    public String deleteContact(String id) {
        Contact c = findById(id.trim());
        if (c == null) {
            return "No contact with ID " + id;
        }
        contacts.remove(c);
        storage.save(contacts);
        return c.getName() + " removed.";
    }

    public List<Contact> getAllContacts() {
        return Collections.unmodifiableList(contacts);
    }

    public List<Contact> search(String query) {
        String q = query.trim().toLowerCase();
        if (q.isEmpty()) return getAllContacts();

        return contacts.stream()
            .filter(c ->
                c.getName().toLowerCase().contains(q) ||
                c.getPhone().contains(q) ||
                c.getEmail().toLowerCase().contains(q)
            )
            .collect(Collectors.toList());
    }

    public Contact findById(String id) {
        return contacts.stream()
            .filter(c -> c.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    private boolean validPhone(String phone) {
        return phone.matches("[0-9+\\-\\s]+");
    }

    public int count() {
        return contacts.size();
    }
}
