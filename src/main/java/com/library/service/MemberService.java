package com.library.service;

import com.library.model.Member;
import com.library.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> getMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    public List<Member> searchMembers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return memberRepository.findAll();
        }
        return memberRepository.search(query.trim());
    }

    public List<Member> getActiveMembers() {
        return memberRepository.findByStatus(Member.MemberStatus.ACTIVE);
    }

    public boolean emailExists(String email, Long excludeId) {
        Optional<Member> existing = memberRepository.findByEmail(email);
        return existing.isPresent() && !existing.get().getId().equals(excludeId);
    }
}
