package com.library;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.Member;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.MemberRepository;
import com.library.service.BookService;
import com.library.service.BorrowService;
import com.library.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LibraryApplicationTests {

    @Autowired
    private BookService bookService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Test
    void contextLoads() {
        assertNotNull(bookService);
        assertNotNull(memberService);
        assertNotNull(borrowService);
    }

    @Test
    void shouldSaveAndRetrieveBook() {
        Book book = new Book("Test Title", "Test Author", "ISBN-TEST-001",
                "Fiction", "Test Publisher", 2023, 3);
        Book saved = bookService.saveBook(book);

        assertNotNull(saved.getId());
        Optional<Book> found = bookService.getBookById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Test Title", found.get().getTitle());
        assertEquals(3, found.get().getAvailableCopies());
    }

    @Test
    void shouldSaveAndRetrieveMember() {
        Member member = new Member("Jane Doe", "jane.doe@test.com", "555-9999", "Test Address");
        Member saved = memberService.saveMember(member);

        assertNotNull(saved.getId());
        Optional<Member> found = memberService.getMemberById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Jane Doe", found.get().getName());
        assertEquals(Member.MemberStatus.ACTIVE, found.get().getStatus());
    }

    @Test
    void shouldBorrowBookAndDecrementAvailableCopies() {
        Book book = bookService.saveBook(new Book("Borrow Test Book", "Author", "ISBN-BT-001",
                "Test", "Publisher", 2020, 2));
        Member member = memberService.saveMember(new Member("Borrower", "borrower@test.com",
                "555-0001", "123 Test St"));

        BorrowRecord record = borrowService.borrowBook(book, member);

        assertNotNull(record.getId());
        assertEquals(BorrowRecord.BorrowStatus.BORROWED, record.getStatus());
        assertNotNull(record.getDueDate());

        Book updatedBook = bookService.getBookById(book.getId()).orElseThrow();
        assertEquals(1, updatedBook.getAvailableCopies());
    }

    @Test
    void shouldReturnBookAndIncrementAvailableCopies() {
        Book book = bookService.saveBook(new Book("Return Test Book", "Author", "ISBN-RT-001",
                "Test", "Publisher", 2021, 1));
        Member member = memberService.saveMember(new Member("Returner", "returner@test.com",
                "555-0002", "456 Test Ave"));

        BorrowRecord borrowed = borrowService.borrowBook(book, member);
        BorrowRecord returned = borrowService.returnBook(borrowed.getId());

        assertEquals(BorrowRecord.BorrowStatus.RETURNED, returned.getStatus());
        assertNotNull(returned.getReturnDate());

        Book updatedBook = bookService.getBookById(book.getId()).orElseThrow();
        assertEquals(1, updatedBook.getAvailableCopies());
    }

    @Test
    void shouldThrowExceptionWhenBookNotAvailable() {
        Book book = bookService.saveBook(new Book("Unavailable Book", "Author", "ISBN-UA-001",
                "Test", "Publisher", 2022, 1));
        book.setAvailableCopies(0);
        bookService.saveBook(book);

        Member member = memberService.saveMember(new Member("Test User", "testuser@test.com",
                "555-0003", "789 Test Blvd"));

        assertThrows(IllegalStateException.class, () -> borrowService.borrowBook(book, member));
    }

    @Test
    void shouldSearchBooksWithQuery() {
        bookService.saveBook(new Book("Spring Boot Guide", "Expert Author", "ISBN-SB-001",
                "Technology", "Tech Press", 2022, 1));

        List<Book> results = bookService.searchBooks("Spring Boot");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(b -> b.getTitle().contains("Spring Boot")));
    }

    @Test
    void shouldSearchMembersWithQuery() {
        memberService.saveMember(new Member("SearchableName User", "searchable@test.com",
                "555-0004", "Test St"));

        List<Member> results = memberService.searchMembers("SearchableName");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(m -> m.getName().contains("SearchableName")));
    }

    @Test
    void shouldDetectDuplicateIsbn() {
        Book book = bookService.saveBook(new Book("Dup Book", "Author", "ISBN-DUP-001",
                "Fiction", "Publisher", 2020, 1));

        assertTrue(bookService.isbnExists("ISBN-DUP-001", null));
        assertFalse(bookService.isbnExists("ISBN-DUP-001", book.getId()));
        assertFalse(bookService.isbnExists("ISBN-NONEXISTENT-999", null));
    }

    @Test
    void shouldDetectDuplicateEmail() {
        Member member = memberService.saveMember(new Member("Dup Member", "dup@test.com",
                "555-0005", "Test Address"));

        assertTrue(memberService.emailExists("dup@test.com", null));
        assertFalse(memberService.emailExists("dup@test.com", member.getId()));
        assertFalse(memberService.emailExists("nonexistent@test.com", null));
    }

    @Test
    void shouldEnforceMaxBorrowLimit() {
        Book[] books = new Book[6];
        for (int i = 0; i < 6; i++) {
            books[i] = bookService.saveBook(new Book("Book " + i, "Author", "ISBN-LIMIT-00" + i,
                    "Test", "Publisher", 2020, 1));
        }
        Member member = memberService.saveMember(new Member("Power Borrower", "powerborrower@test.com",
                "555-0006", "Test Address"));

        for (int i = 0; i < 5; i++) {
            borrowService.borrowBook(books[i], member);
        }

        assertThrows(IllegalStateException.class, () -> borrowService.borrowBook(books[5], member));
    }
}
