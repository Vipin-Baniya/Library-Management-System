package com.library.controller;

import com.library.model.BorrowRecord;
import com.library.service.BookService;
import com.library.service.BorrowService;
import com.library.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final BookService bookService;
    private final MemberService memberService;
    private final BorrowService borrowService;

    @Autowired
    public HomeController(BookService bookService, MemberService memberService,
                          BorrowService borrowService) {
        this.bookService = bookService;
        this.memberService = memberService;
        this.borrowService = borrowService;
    }

    @GetMapping("/")
    public String home(Model model) {
        long totalBooks = bookService.getAllBooks().size();
        long availableBooks = bookService.getAvailableBooks().size();
        long totalMembers = memberService.getAllMembers().size();
        long activeMembers = memberService.getActiveMembers().size();
        List<BorrowRecord> activeBorrows = borrowService.getActiveBorrows();
        List<BorrowRecord> overdueBorrows = borrowService.getOverdueBorrows();

        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("availableBooks", availableBooks);
        model.addAttribute("totalMembers", totalMembers);
        model.addAttribute("activeMembers", activeMembers);
        model.addAttribute("activeBorrowCount", activeBorrows.size());
        model.addAttribute("overdueBorrowCount", overdueBorrows.size());
        model.addAttribute("recentBorrows", activeBorrows.stream().limit(5).toList());

        return "index";
    }
}
