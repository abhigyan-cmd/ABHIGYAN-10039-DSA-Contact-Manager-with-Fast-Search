
import java.io.*;
import java.util.*;

public class Main {

    static Scanner sc = new Scanner(System.in);

    static class Contact {
        String id, name, phone, email;

        Contact(String id, String name, String phone, String email) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.email = email;
        }

        public String toString() {
            return id + " | " + name + " | " + phone + " | " + email;
        }
    }

    static class BST {
        class Node {
            Contact contact;
            Node left, right;

            Node(Contact contact) {
                this.contact = contact;
            }
        }

        Node root;

        void insert(Contact c) {
            root = insertNode(root, c);
        }

        Node insertNode(Node root, Contact c) {
            if (root == null)
                return new Node(c);

            int result = c.name.compareToIgnoreCase(root.contact.name);

            if (result < 0)
                root.left = insertNode(root.left, c);
            else if (result > 0)
                root.right = insertNode(root.right, c);
            else
                root.contact = c;

            return root;
        }

        Contact search(String name) {
            Node current = root;

            while (current != null) {
                int result = name.compareToIgnoreCase(current.contact.name);

                if (result == 0)
                    return current.contact;
                else if (result < 0)
                    current = current.left;
                else
                    current = current.right;
            }

            return null;
        }

        void displaySorted() {
            inorder(root);
        }

        void inorder(Node root) {
            if (root != null) {
                inorder(root.left);
                System.out.println(root.contact);
                inorder(root.right);
            }
        }
    }

    static class ContactManager {

        LinkedList<Contact> contacts = new LinkedList<>();
        HashMap<String, Contact> phoneMap = new HashMap<>();
        BST bst = new BST();

        int nextId = 101;
        String fileName = "contacts.txt";

        ContactManager() {
            loadContacts();
        }

        boolean validPhone(String phone) {
            return phone.matches("\\d{10}");
        }

        boolean validEmail(String email) {
            return email.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
            );
        }

        void rebuildIndexes() {
            bst = new BST();
            phoneMap.clear();

            for (Contact c : contacts) {
                bst.insert(c);
                phoneMap.put(c.phone, c);
            }
        }

        void addContact() {

            System.out.print("Enter Name: ");
            String name = sc.nextLine().trim();

            System.out.print("Enter Phone Number: ");
            String phone = sc.nextLine().trim();

            System.out.print("Enter Email: ");
            String email = sc.nextLine().trim();

            if (name.isEmpty() || phone.contains("|")
                    || email.contains("|")
                    || !validPhone(phone) || !validEmail(email)) {
                System.out.println("Invalid contact details!");
                return;
            }

            if (phoneMap.containsKey(phone)) {
                System.out.println("Phone number already exists!");
                return;
            }

            String id = "C" + nextId++;

            Contact c = new Contact(id, name, phone, email);

            contacts.add(c);
            bst.insert(c);
            phoneMap.put(phone, c);

            saveContacts();

            System.out.println("Contact added: " + c);
        }

        void displayContacts() {

            if (contacts.isEmpty()) {
                System.out.println("No contacts found.");
                return;
            }

            System.out.println("\n--- CONTACT DIRECTORY ---");
            bst.displaySorted();
        }

        void searchByName() {

            System.out.print("Enter Name: ");
            String name = sc.nextLine().trim();

            Contact c = bst.search(name);

            if (c != null)
                System.out.println("Contact Found: " + c);
            else
                System.out.println("Contact not found.");
        }

        void searchByPhone() {

            System.out.print("Enter Phone Number: ");
            String phone = sc.nextLine().trim();

            Contact c = phoneMap.get(phone);

            if (c != null)
                System.out.println("Contact Found: " + c);
            else
                System.out.println("Contact not found.");
        }

        void updateContact() {

            System.out.print("Enter Current Phone Number: ");
            String phone = sc.nextLine().trim();

            Contact c = phoneMap.get(phone);

            if (c == null) {
                System.out.println("Contact not found.");
                return;
            }

            System.out.print("Enter New Name: ");
            String name = sc.nextLine().trim();

            System.out.print("Enter New Phone: ");
            String newPhone = sc.nextLine().trim();

            System.out.print("Enter New Email: ");
            String email = sc.nextLine().trim();

            if (name.isEmpty() || newPhone.contains("|")
                    || email.contains("|")
                    || !validPhone(newPhone) || !validEmail(email)) {
                System.out.println("Invalid details!");
                return;
            }

            if (phoneMap.containsKey(newPhone)
                    && phoneMap.get(newPhone) != c) {
                System.out.println("Phone number already exists!");
                return;
            }

            c.name = name;
            c.phone = newPhone;
            c.email = email;

            rebuildIndexes();
            saveContacts();

            System.out.println("Contact updated successfully!");
        }

        void deleteContact() {

            System.out.print("Enter Phone Number: ");
            String phone = sc.nextLine().trim();

            Contact c = phoneMap.get(phone);

            if (c == null) {
                System.out.println("Contact not found.");
                return;
            }

            contacts.remove(c);
            rebuildIndexes();
            saveContacts();

            System.out.println("Contact deleted successfully!");
        }

        void sortContacts() {

            if (contacts.isEmpty()) {
                System.out.println("No contacts available.");
                return;
            }

            ArrayList<Contact> sorted =
                new ArrayList<>(contacts);

            Collections.sort(sorted,
                (a, b) -> a.name.compareToIgnoreCase(b.name));

            System.out.println("\n--- SORTED CONTACT REPORT ---");

            for (Contact c : sorted) {
                System.out.println(c);
            }
        }

        void saveContacts() {

            try (BufferedWriter writer =
                    new BufferedWriter(new FileWriter(fileName))) {

                for (Contact c : contacts) {
                    writer.write(c.id + "|" + c.name + "|"
                            + c.phone + "|" + c.email);
                    writer.newLine();
                }

            } catch (IOException e) {
                System.out.println("Error saving contacts.");
            }
        }

        void loadContacts() {

            File file = new File(fileName);

            if (!file.exists())
                return;

            try (BufferedReader reader =
                    new BufferedReader(new FileReader(file))) {

                String line;

                while ((line = reader.readLine()) != null) {

                    String[] data = line.split("\\|", -1);

                    if (data.length != 4)
                        continue;

                    if (data[0].trim().isEmpty()
                            || data[1].trim().isEmpty()
                            || !validPhone(data[2])
                            || !validEmail(data[3])
                            || phoneMap.containsKey(data[2]))
                        continue;

                    Contact c = new Contact(
                        data[0], data[1], data[2], data[3]
                    );

                    contacts.add(c);
                    bst.insert(c);
                    phoneMap.put(c.phone, c);

                    try {
                        int number = Integer.parseInt(
                            c.id.substring(1)
                        );

                        if (number >= nextId)
                            nextId = number + 1;

                    } catch (Exception ignored) {
                    }
                }

                System.out.println(
                    contacts.size() + " contact(s) loaded."
                );

            } catch (IOException e) {
                System.out.println("Error loading contacts.");
            }
        }
    }

    public static void main(String[] args) {

        ContactManager manager = new ContactManager();

        while (true) {

            System.out.println("\n================================");
            System.out.println(" CONTACT MANAGER WITH FAST SEARCH");
            System.out.println("================================");
            System.out.println("1. Add Contact");
            System.out.println("2. Display Contacts");
            System.out.println("3. Search By Name");
            System.out.println("4. Search By Phone");
            System.out.println("5. Update Contact");
            System.out.println("6. Delete Contact");
            System.out.println("7. Sort Contacts");
            System.out.println("0. Save and Exit");

            System.out.print("Enter Your Choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {

                case "1":
                    manager.addContact();
                    break;

                case "2":
                    manager.displayContacts();
                    break;

                case "3":
                    manager.searchByName();
                    break;

                case "4":
                    manager.searchByPhone();
                    break;

                case "5":
                    manager.updateContact();
                    break;

                case "6":
                    manager.deleteContact();
                    break;

                case "7":
                    manager.sortContacts();
                    break;

                case "0":
                    manager.saveContacts();
                    System.out.println("Data saved successfully!");
                    System.out.println("Thank You!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
