package com.library.controller;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.Member;
import com.library.service.BookService;
import com.library.service.BorrowService;
import com.library.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/borrows")
public class BorrowController {

    private final BorrowService borrowService;
    private final BookService bookService;
    private final MemberService memberService;

    @Autowired
    public BorrowController(BorrowService borrowService, BookService bookService,
                             MemberService memberService) {
        this.borrowService = borrowService;
        this.bookService = bookService;
        this.memberService = memberService;
    }

    @GetMapping
    public String listBorrows(@RequestParam(required = false) String filter, Model model) {
        List<BorrowRecord> borrows;
        if ("overdue".equals(filter)) {
            borrows = borrowService.getOverdueBorrows();
            model.addAttribute("filter", "overdue");
        } else if ("returned".equals(filter)) {
            borrows = borrowService.getAllBorrowRecords().stream()
                    .filter(b -> b.getStatus() == BorrowRecord.BorrowStatus.RETURNED)
                    .toList();
            model.addAttribute("filter", "returned");
        } else {
            borrows = borrowService.getActiveBorrows();
            model.addAttribute("filter", "active");
        }
        model.addAttribute("borrows", borrows);
        return "borrows/list";
    }

    @GetMapping("/new")
    public String newBorrowForm(Model model) {
        model.addAttribute("availableBooks", bookService.getAvailableBooks());
        model.addAttribute("activeMembers", memberService.getActiveMembers());
        return "borrows/form";
    }

    @PostMapping("/new")
    public String issueBorrow(@RequestParam Long bookId, @RequestParam Long memberId,
                               RedirectAttributes redirectAttributes) {
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found."));
        Member member = memberService.getMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        try {
            borrowService.borrowBook(book, member);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Book '" + book.getTitle() + "' successfully issued to " + member.getName() + ".");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/borrows";
    }

    @PostMapping("/{id}/return")
    public String returnBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            BorrowRecord record = borrowService.returnBook(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Book '" + record.getBook().getTitle() + "' returned successfully.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/borrows";
    }
}
