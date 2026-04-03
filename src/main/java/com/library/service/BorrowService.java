package com.library.service;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.Member;
import com.library.repository.BorrowRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BorrowService {

    private static final int MAX_BORROW_LIMIT = 5;
    private static final int DEFAULT_LOAN_DAYS = 14;

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookService bookService;

    @Autowired
    public BorrowService(BorrowRecordRepository borrowRecordRepository, BookService bookService) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookService = bookService;
    }

    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordRepository.findAll();
    }

    public Optional<BorrowRecord> getBorrowRecordById(Long id) {
        return borrowRecordRepository.findById(id);
    }

    public List<BorrowRecord> getBorrowsByMember(Member member) {
        return borrowRecordRepository.findByMember(member);
    }

    public List<BorrowRecord> getBorrowsByBook(Book book) {
        return borrowRecordRepository.findByBook(book);
    }

    public List<BorrowRecord> getActiveBorrows() {
        return borrowRecordRepository.findByStatus(BorrowRecord.BorrowStatus.BORROWED);
    }

    public List<BorrowRecord> getOverdueBorrows() {
        updateOverdueRecords();
        return borrowRecordRepository.findByStatus(BorrowRecord.BorrowStatus.OVERDUE);
    }

    public BorrowRecord borrowBook(Book book, Member member) {
        if (!book.isAvailable()) {
            throw new IllegalStateException("No copies of '" + book.getTitle() + "' are available.");
        }
        if (member.getStatus() != Member.MemberStatus.ACTIVE) {
            throw new IllegalStateException("Member account is not active.");
        }
        long activeBorrows = borrowRecordRepository.countActiveBorrowsByMember(member);
        if (activeBorrows >= MAX_BORROW_LIMIT) {
            throw new IllegalStateException(
                    "Member has reached the maximum borrow limit of " + MAX_BORROW_LIMIT + " books.");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookService.saveBook(book);

        LocalDate dueDate = LocalDate.now().plusDays(DEFAULT_LOAN_DAYS);
        BorrowRecord record = new BorrowRecord(book, member, dueDate);
        return borrowRecordRepository.save(record);
    }

    public BorrowRecord returnBook(Long borrowRecordId) {
        BorrowRecord record = borrowRecordRepository.findById(borrowRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Borrow record not found."));

        if (record.getStatus() == BorrowRecord.BorrowStatus.RETURNED) {
            throw new IllegalStateException("This book has already been returned.");
        }

        record.setReturnDate(LocalDate.now());
        record.setStatus(BorrowRecord.BorrowStatus.RETURNED);

        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookService.saveBook(book);

        return borrowRecordRepository.save(record);
    }

    private void updateOverdueRecords() {
        List<BorrowRecord> overdue = borrowRecordRepository.findByStatusAndDueDateBefore(
                BorrowRecord.BorrowStatus.BORROWED, LocalDate.now());
        overdue.forEach(r -> {
            r.setStatus(BorrowRecord.BorrowStatus.OVERDUE);
            borrowRecordRepository.save(r);
        });
    }
}
