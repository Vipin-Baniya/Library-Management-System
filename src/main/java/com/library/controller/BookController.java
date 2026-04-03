package com.library.controller;

import com.library.model.Book;
import com.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String listBooks(@RequestParam(required = false) String query, Model model) {
        List<Book> books;
        if (query != null && !query.trim().isEmpty()) {
            books = bookService.searchBooks(query);
            model.addAttribute("query", query);
        } else {
            books = bookService.getAllBooks();
        }
        model.addAttribute("books", books);
        return "books/list";
    }

    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
        model.addAttribute("book", book);
        return "books/detail";
    }

    @GetMapping("/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("pageTitle", "Add New Book");
        return "books/form";
    }

    @PostMapping("/new")
    public String createBook(@Valid @ModelAttribute Book book, BindingResult result,
                              RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Add New Book");
            return "books/form";
        }
        if (bookService.isbnExists(book.getIsbn(), null)) {
            result.rejectValue("isbn", "duplicate", "A book with this ISBN already exists.");
            model.addAttribute("pageTitle", "Add New Book");
            return "books/form";
        }
        bookService.saveBook(book);
        redirectAttributes.addFlashAttribute("successMessage", "Book added successfully!");
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String editBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
        model.addAttribute("book", book);
        model.addAttribute("pageTitle", "Edit Book");
        return "books/form";
    }

    @PostMapping("/{id}/edit")
    public String updateBook(@PathVariable Long id, @Valid @ModelAttribute Book book,
                              BindingResult result, RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Book");
            return "books/form";
        }
        if (bookService.isbnExists(book.getIsbn(), id)) {
            result.rejectValue("isbn", "duplicate", "A book with this ISBN already exists.");
            model.addAttribute("pageTitle", "Edit Book");
            return "books/form";
        }
        book.setId(id);
        bookService.saveBook(book);
        redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully!");
        return "redirect:/books";
    }

    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookService.deleteBook(id);
        redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully!");
        return "redirect:/books";
    }
}
