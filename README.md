# Phonebook CLI

A command-line phonebook application written in plain Java. No frameworks, no external libraries, no REST API — just Java SE and the standard library.

---

## How to Run

**Prerequisites:** JDK 8 or above installed (`javac` and `java` on your PATH).

**Compile:**
```bash
mkdir -p out
javac -d out src/phonebook/*.java
```

**Run:**
```bash
java -cp out phonebook.Main
```

By default, contacts are saved to `contacts.json` in the current directory. To use a different file:
```bash
java -cp out phonebook.Main /path/to/mycontacts.json
```

---

## Features

| Feature         | Details                                                  |
|----------------|----------------------------------------------------------|
| Add contact     | Name + phone (required), email (optional)               |
| Update contact  | Edit name, phone, and/or email by contact ID            |
| Delete contact  | Remove a contact by ID, with confirmation prompt        |
| View all        | Tabular list of all saved contacts                      |
| Search          | Case-insensitive partial match on name, phone, or email |

---

## Project Structure

```
phonebook/
├── src/
│   └── phonebook/
│       ├── Contact.java          # Data model
│       ├── FileStorage.java      # JSON read/write
│       ├── PhonebookService.java # Business logic
│       └── Main.java             # CLI and entry point
├── build.sh                      # Compile script
└── contacts.json                 # Created on first run
```

---

## Design Decisions

**No external libraries**
The JSON format used is a flat array of objects, simple enough to parse with a hand-rolled reader. Adding Gson or Jackson for something this constrained felt like overkill, and keeping it dependency-free means anyone can compile and run it with just a JDK.

**File-based storage**
Contacts persist to `contacts.json` after every write operation (add, update, delete). I considered in-memory only but file storage means you don't lose your data between sessions, which is the more useful default for a real tool.

**Auto-incrementing integer IDs**
IDs start at 1 and increment. On startup the app reads the existing file and continues from the highest ID found, so IDs never collide even after contacts are deleted.

**Validation**
- Name and phone are required; email is optional.
- Phone numbers must contain only digits, spaces, `+`, or hyphens.
- Duplicate phone numbers are rejected.

**Search**
Partial, case-insensitive match across all three fields. Searching `ali` will match `Alice`, `Malik`, `malika@example.com`, etc.

**Separation of concerns**
The CLI (`Main.java`) only handles input/output. All logic sits in `PhonebookService`, and all file I/O in `FileStorage`. This makes it straightforward to swap out the storage layer or add a different interface later without touching the core logic.

---

## Sample Session

```
=========================================
          PHONEBOOK APPLICATION          
=========================================
  Contacts stored in: contacts.json

  1. Add contact
  2. Update contact
  3. Delete contact
  4. View all contacts
  5. Search
  6. Exit
  Choose an option: 1

-- Add Contact --
  Name: Alice Sharma
  Phone: 9876543210
  Email (leave blank to skip): alice@example.com
  Contact added (ID: 1).

  Choose an option: 5

-- Search --
  Search by name, phone, or email: alice

  Found 1 result(s):

  ------  --------------------  ---------------  --------------------
  ID     Name                 Phone            Email
  ------  --------------------  ---------------  --------------------
  1      Alice Sharma         9876543210       alice@example.com
```

---

## Data Storage

Contacts are stored as a JSON array in `contacts.json`:

```json
[
  {"id":"1","name":"Alice Sharma","phone":"9876543210","email":"alice@example.com"},
  {"id":"2","name":"Raj Patil","phone":"9823001122","email":""}
]
```

The file is created automatically on the first write. If it doesn't exist at startup, the app begins with an empty contact list.
