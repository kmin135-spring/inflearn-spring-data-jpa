package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepo;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepo.findById(id).get();
        return member.getUsername();
    }

    /**
     * 도메인 클래스 컨버터
     * 편리해보이지만 명시적이지 않기 때문에 간단한 경우에만 사용 권장
     * 사용하더라도 조회 only 정도로만 권장
     * */
    @GetMapping("/members2/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @PostConstruct
    public void init() {
        memberRepo.save(Member.of("testuser", 25));
    }
}
