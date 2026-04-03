package com.library.controller;

import com.library.model.Member;
import com.library.service.BorrowService;
import com.library.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final BorrowService borrowService;

    @Autowired
    public MemberController(MemberService memberService, BorrowService borrowService) {
        this.memberService = memberService;
        this.borrowService = borrowService;
    }

    @GetMapping
    public String listMembers(@RequestParam(required = false) String query, Model model) {
        List<Member> members;
        if (query != null && !query.trim().isEmpty()) {
            members = memberService.searchMembers(query);
            model.addAttribute("query", query);
        } else {
            members = memberService.getAllMembers();
        }
        model.addAttribute("members", members);
        return "members/list";
    }

    @GetMapping("/{id}")
    public String viewMember(@PathVariable Long id, Model model) {
        Member member = memberService.getMemberById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + id));
        model.addAttribute("member", member);
        model.addAttribute("borrowHistory", borrowService.getBorrowsByMember(member));
        return "members/detail";
    }

    @GetMapping("/new")
    public String newMemberForm(Model model) {
        model.addAttribute("member", new Member());
        model.addAttribute("pageTitle", "Add New Member");
        return "members/form";
    }

    @PostMapping("/new")
    public String createMember(@Valid @ModelAttribute Member member, BindingResult result,
                                RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Add New Member");
            return "members/form";
        }
        if (memberService.emailExists(member.getEmail(), null)) {
            result.rejectValue("email", "duplicate", "A member with this email already exists.");
            model.addAttribute("pageTitle", "Add New Member");
            return "members/form";
        }
        memberService.saveMember(member);
        redirectAttributes.addFlashAttribute("successMessage", "Member registered successfully!");
        return "redirect:/members";
    }

    @GetMapping("/{id}/edit")
    public String editMemberForm(@PathVariable Long id, Model model) {
        Member member = memberService.getMemberById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + id));
        model.addAttribute("member", member);
        model.addAttribute("pageTitle", "Edit Member");
        return "members/form";
    }

    @PostMapping("/{id}/edit")
    public String updateMember(@PathVariable Long id, @Valid @ModelAttribute Member member,
                                BindingResult result, RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Member");
            return "members/form";
        }
        if (memberService.emailExists(member.getEmail(), id)) {
            result.rejectValue("email", "duplicate", "A member with this email already exists.");
            model.addAttribute("pageTitle", "Edit Member");
            return "members/form";
        }
        member.setId(id);
        memberService.saveMember(member);
        redirectAttributes.addFlashAttribute("successMessage", "Member updated successfully!");
        return "redirect:/members";
    }

    @PostMapping("/{id}/delete")
    public String deleteMember(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        memberService.deleteMember(id);
        redirectAttributes.addFlashAttribute("successMessage", "Member removed successfully!");
        return "redirect:/members";
    }
}
