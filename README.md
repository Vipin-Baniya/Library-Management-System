# Library Management System

A full-stack **Library Management System** built with **Java**, **Spring Boot**, **Spring Data JPA**, **Thymeleaf**, and **H2** (in-memory database).

---

## Features

- 📚 **Book Management** — Add, edit, delete, and search books (title, author, ISBN)
- 👤 **Member Management** — Register, edit, remove library members with status tracking
- 🔄 **Borrow & Return** — Issue books to members, track due dates, and process returns
- ⚠️ **Overdue Detection** — Automatically flags overdue borrow records
- 📊 **Dashboard** — Summary of total books, available copies, members, active borrows, and overdue items
- 🔍 **Search** — Search books and members from list views
- ✅ **Validation** — Form validation with user-friendly error messages

---

## Technology Stack

| Layer      | Technology                        |
|------------|-----------------------------------|
| Language   | Java 17                           |
| Framework  | Spring Boot 3.x                   |
| Persistence| Spring Data JPA / Hibernate       |
| Database   | H2 (in-memory, dev/test)          |
| Frontend   | Thymeleaf + Bootstrap 5           |
| Build      | Maven                             |
| Tests      | JUnit 5 / Spring Boot Test        |

---

## Project Structure

```
src/
├── main/
│   ├── java/com/library/
│   │   ├── LibraryApplication.java       # Spring Boot entry point
│   │   ├── model/
│   │   │   ├── Book.java                 # Book entity
│   │   │   ├── Member.java               # Member entity
│   │   │   └── BorrowRecord.java         # Borrow record entity
│   │   ├── repository/
│   │   │   ├── BookRepository.java
│   │   │   ├── MemberRepository.java
│   │   │   └── BorrowRecordRepository.java
│   │   ├── service/
│   │   │   ├── BookService.java
│   │   │   ├── MemberService.java
│   │   │   └── BorrowService.java
│   │   └── controller/
│   │       ├── HomeController.java       # Dashboard
│   │       ├── BookController.java
│   │       ├── MemberController.java
│   │       └── BorrowController.java
│   └── resources/
│       ├── application.properties
│       ├── data.sql                      # Sample seed data
│       └── templates/
│           ├── index.html                # Dashboard
│           ├── books/  (list, form, detail)
│           ├── members/ (list, form, detail)
│           └── borrows/ (list, form)
└── test/
    └── java/com/library/
        └── LibraryApplicationTests.java  # Integration tests
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+

### Run the Application

```bash
# Clone the repository
git clone https://github.com/Vipin-Baniya/Library-Management-System.git
cd Library-Management-System

# Build and run
mvn spring-boot:run
```

Open your browser at: **http://localhost:8080**

### Run Tests

```bash
mvn test
```

### H2 Database Console

Access the in-memory database at: **http://localhost:8080/h2-console**

- JDBC URL: `jdbc:h2:mem:librarydb`
- Username: `sa`
- Password: *(leave blank)*

---

## Business Rules

- A member can borrow a maximum of **5 books** at a time.
- Default loan period is **14 days**.
- A member must be **ACTIVE** to borrow books.
- Books with 0 available copies cannot be borrowed.
- Overdue status is automatically detected based on the due date.
