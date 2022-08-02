package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
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
        for(int i=0;i<100;i++) {
            memberRepo.save(Member.of("testuser" + i, 10+i));
        }
    }

    /**
     * 기본 페이징 & 정렬 제공
     * ex1) members?page=1&size=3
     * ex2) members?page=1&size=3&sort=id,desc&sort=username,desc
     */
    @GetMapping("/members")
    public Page<Member> list(Pageable pageable) {
        System.out.println(pageable.getClass());
        return memberRepo.findAll(pageable);
    }

    /**
     * 실무에서는 entity를 그대로 리턴하지마라
     * dto로 변환해서 리턴
     */
    @GetMapping("/members-dto")
    public Page<MemberDto> listDto(Pageable pageable) {
        return memberRepo.findAll(pageable).map(m -> new MemberDto(m.getId(), m.getUsername(), null));
    }

    /**
     * yml의 글로벌 설정보다 @PageableDefault 로 요청별로 기본값을 설정할 수 있음
     */
    @GetMapping("/customer-members")
    public Page<Member> customList(@PageableDefault(size = 5, sort = "username") Pageable pageable) {
        System.out.println(pageable.getClass());
        return memberRepo.findAll(pageable);
    }
}
