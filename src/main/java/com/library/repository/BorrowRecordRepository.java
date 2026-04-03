package com.library.repository;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    List<BorrowRecord> findByMember(Member member);

    List<BorrowRecord> findByBook(Book book);

    List<BorrowRecord> findByStatus(BorrowRecord.BorrowStatus status);

    Optional<BorrowRecord> findByBookAndMemberAndStatus(Book book, Member member,
                                                         BorrowRecord.BorrowStatus status);

    List<BorrowRecord> findByStatusAndDueDateBefore(BorrowRecord.BorrowStatus status, LocalDate date);

    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.member = :member AND br.status = 'BORROWED'")
    long countActiveBorrowsByMember(Member member);
}
