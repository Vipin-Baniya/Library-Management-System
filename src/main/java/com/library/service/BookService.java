package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return bookRepository.findAll();
        }
        return bookRepository.search(query.trim());
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0);
    }

    public List<Book> getBooksByGenre(String genre) {
        return bookRepository.findByGenreIgnoreCase(genre);
    }

    public boolean isbnExists(String isbn, Long excludeId) {
        Optional<Book> existing = bookRepository.findByIsbn(isbn);
        return existing.isPresent() && !existing.get().getId().equals(excludeId);
    }
}
