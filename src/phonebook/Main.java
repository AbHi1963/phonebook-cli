package phonebook;

import java.util.*;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static PhonebookService service;

    public static void main(String[] args) {
        String file = args.length > 0 ? args[0] : "contacts.json";
        service = new PhonebookService(new FileStorage(file));

        System.out.println("\n=== Phonebook ===");
        System.out.println("Data file: " + file + "\n");

        boolean running = true;
        while (running) {
            showMenu();
            String input = read("").trim();

            switch (input) {
                case "1": doAdd();    break;
                case "2": doUpdate(); break;
                case "3": doDelete(); break;
                case "4": doList();   break;
                case "5": doSearch(); break;
                case "6":
                    System.out.println("Bye.");
                    running = false;
                    break;
                default:
                    System.out.println("Enter a number between 1 and 6.\n");
            }
        }

        sc.close();
    }

    private static void doAdd() {
        System.out.println("\nNew contact");
        String name  = read("Name");
        String phone = read("Phone");
        String email = read("Email (optional)");
        System.out.println(service.addContact(name, phone, email) + "\n");
    }

    private static void doUpdate() {
        if (service.count() == 0) {
            System.out.println("No contacts yet.\n");
            return;
        }

        printTable(service.getAllContacts());
        String id = read("ID to edit");
        System.out.println("Leave blank to keep existing value.");

        String name  = read("Name");
        String phone = read("Phone");
        String email = read("Email");
        System.out.println(service.updateContact(id, name, phone, email) + "\n");
    }

    private static void doDelete() {
        if (service.count() == 0) {
            System.out.println("No contacts yet.\n");
            return;
        }

        printTable(service.getAllContacts());
        String id = read("ID to delete");
        String confirm = read("Sure? y/n");

        if (confirm.equalsIgnoreCase("y")) {
            System.out.println(service.deleteContact(id) + "\n");
        } else {
            System.out.println("Cancelled.\n");
        }
    }

    private static void doList() {
        List<Contact> all = service.getAllContacts();
        if (all.isEmpty()) {
            System.out.println("No contacts saved.\n");
            return;
        }
        printTable(all);
    }

    private static void doSearch() {
        String q = read("Search");
        List<Contact> results = service.search(q);

        if (results.isEmpty()) {
            System.out.println("Nothing found for \"" + q + "\"\n");
        } else {
            System.out.println(results.size() + " result(s):");
            printTable(results);
        }
    }

    private static void printTable(List<Contact> list) {
        String line = "------  ----------------------  ----------------  --------------------";
        System.out.println(line);
        System.out.println("ID      Name                    Phone             Email");
        System.out.println(line);
        for (Contact c : list) {
            System.out.println(c);
        }
        System.out.println(line + "\n");
    }

    private static void showMenu() {
        System.out.println("1. Add");
        System.out.println("2. Edit");
        System.out.println("3. Delete");
        System.out.println("4. View all");
        System.out.println("5. Search");
        System.out.println("6. Exit");
    }

    private static String read(String label) {
        if (!label.isEmpty()) System.out.print(label + ": ");
        else System.out.print("> ");
        return sc.nextLine();
    }
}
