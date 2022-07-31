package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired MemberRepository mRepo;
    @Autowired TeamRepository tRepo;
    @Autowired EntityManager em;

    @Test
    void testMember() {
        // 구현체 = class com.sun.proxy.$Proxy122
        System.out.println("구현체 = " + mRepo.getClass());
        Member m = Member.of("memberA", 20);
        Member saved = mRepo.save(m);
        Member found = mRepo.findById(saved.getId()).get();

        assertThat(found).isEqualTo(saved);
    }

    @Test
    void basicCRUD() {
        Member m1 = Member.of("member1", 10);
        Member m2 = Member.of("member2", 20);
        mRepo.save(m1);
        mRepo.save(m2);

        Member findM1 = mRepo.findById(m1.getId()).get();
        Member findM2 = mRepo.findById(m2.getId()).get();
        assertThat(findM1).isEqualTo(m1);
        assertThat(findM2).isEqualTo(m2);

        List<Member> all = mRepo.findAll();
        assertThat(all.size()).isEqualTo(2);

        mRepo.delete(m1);
        mRepo.delete(m2);

        long currentCnt = mRepo.count();
        assertThat(currentCnt).isEqualTo(0L);
    }

    @Test
    void findByUsernameAndAgeGreaterThan() {
        Member m1 = Member.of("member", 10);
        Member m2 = Member.of("member", 20);
        mRepo.save(m1);
        mRepo.save(m2);

        List<Member> members = mRepo.findByUsernameAndAgeGreaterThan("member", 15);
        assertThat(members.size()).isEqualTo(1);
        assertThat(members.get(0)).isEqualTo(m2);
    }

    @Test
    void testQuery() {
        Member m1 = Member.of("member1", 10);
        Member m2 = Member.of("member2", 20);
        mRepo.save(m1);
        mRepo.save(m2);

        List<Member> members = mRepo.findUser("member1", 10);

        assertThat(members.size()).isEqualTo(1);
        assertThat(members.get(0)).isEqualTo(m1);
    }

    @Test
    void dtoTestQuery() {
        Team t1 = Team.of("team1");
        tRepo.save(t1);

        Member m1 = Member.of("member1", 10);
        m1.changeTeam(t1);
        mRepo.save(m1);

        List<MemberDto> membersDto = mRepo.findMembersDto();

        assertThat(membersDto.size()).isEqualTo(1);
        assertThat(membersDto.get(0).getUsername()).isEqualTo("member1");
        assertThat(membersDto.get(0).getTeamName()).isEqualTo("team1");
    }

    @Test
    void collectionSearch() {
        Member m1 = Member.of("member1", 10);
        Member m2 = Member.of("member2", 20);
        mRepo.save(m1);
        mRepo.save(m2);

        List<Member> members = mRepo.findByNames(Arrays.asList("member1", "member2"));

        assertThat(members.size()).isEqualTo(2);
    }

    @Test
    void returnType() {
        Member m1 = Member.of("member1", 10);
        Member m2 = Member.of("member2", 20);
        mRepo.save(m1);
        mRepo.save(m2);

        // list는 결과가 0개면 empty list 리턴 보장
        List<Member> list = mRepo.findByUsername("member1");
        // 이건 결과가 없으면 null
        // 그냥 JPA 에서는 단건조회에서 없으면 NoResultException 발생하던것과 다름
        // data-jpa 가 이 예외를 감싸서 null로 던지도록 처리한 것
        Member one = mRepo.findOneByUsername("member1");
        // 데이터가 없을 수 있다면 Optional을 쓰는게 맞다.
        Optional<Member> option = mRepo.findOptionalByUsername("member1");

        // 다만 Member든 Optional이든 2건 이상이 나오면 exception인건 공통통

       assertThat(list.size()).isEqualTo(1);
        assertThat(one).isEqualTo(m1);
        assertThat(option.isPresent()).isEqualTo(true);
    }

    @Test
    void paging() {
        // assign
        mRepo.save(Member.of("mem1", 10));
        mRepo.save(Member.of("mem2", 10));
        mRepo.save(Member.of("mem3", 10));
        mRepo.save(Member.of("mem4", 10));
        mRepo.save(Member.of("mem5", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //action
        Page<Member> page = mRepo.findByAge(10, pageRequest);

        /*
        페이징에서도 당연히 Entity를 그대로 노출하지말고
        아래와 같은 방법으로 DTO로 변환해서 반환하자
         */
        Page<MemberDto> dtoPage = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        List<MemberDto> content = dtoPage.getContent();

        //assert
        assertThat(content.size()).isEqualTo(3);
        assertThat(dtoPage.getSize()).isEqualTo(3);
        assertThat(dtoPage.getTotalElements()).isEqualTo(5);
        assertThat(dtoPage.getTotalPages()).isEqualTo(2);
        assertThat(dtoPage.isFirst()).isEqualTo(true);
        assertThat(dtoPage.hasNext()).isEqualTo(true);
        assertThat(dtoPage.hasPrevious()).isEqualTo(false);
    }

    @Test
    void pagingCustomCount() {
        // assign
        mRepo.save(Member.of("mem1", 10));
        mRepo.save(Member.of("mem2", 10));
        mRepo.save(Member.of("mem3", 10));
        mRepo.save(Member.of("mem4", 10));
        mRepo.save(Member.of("mem5", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //action
        Page<Member> page = mRepo.findCustomCountByAge(10, pageRequest);
        Page<MemberDto> dtoPage = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        List<MemberDto> content = dtoPage.getContent();

        //assert
        assertThat(content.size()).isEqualTo(3);
        assertThat(dtoPage.getSize()).isEqualTo(3);
        assertThat(dtoPage.getTotalElements()).isEqualTo(5);
        assertThat(dtoPage.getTotalPages()).isEqualTo(2);
        assertThat(dtoPage.isFirst()).isEqualTo(true);
        assertThat(dtoPage.hasNext()).isEqualTo(true);
        assertThat(dtoPage.hasPrevious()).isEqualTo(false);
    }

    @Test
    void slicing() {
        // assign
        mRepo.save(Member.of("mem1", 10));
        mRepo.save(Member.of("mem2", 10));
        mRepo.save(Member.of("mem3", 10));
        mRepo.save(Member.of("mem4", 10));
        mRepo.save(Member.of("mem5", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //action
        Slice<Member> slicing = mRepo.findSliceByAge(10, pageRequest);
        List<Member> content = slicing.getContent();

        //assert
        assertThat(content.size()).isEqualTo(3);
        assertThat(slicing.getSize()).isEqualTo(3);
        assertThat(slicing.isFirst()).isEqualTo(true);
        assertThat(slicing.hasNext()).isEqualTo(true);
        assertThat(slicing.hasPrevious()).isEqualTo(false);
    }

    @Test
    void bulkAgePlus() {
        // assign
        mRepo.save(Member.of("mem1", 10));
        mRepo.save(Member.of("mem2", 19));
        mRepo.save(Member.of("mem3", 20));
        mRepo.save(Member.of("mem4", 21));
        mRepo.save(Member.of("mem5", 40));

        // action
        // 참고로 JPQL이 나가기 전에 영속성 컨텍스트가 flush됨
        int affected = mRepo.bulkAgePlus(20);

        /*
        bulk 연산 주의점
        영속성 컨텍스트를 무시하고 DB에 바로 반영해버리므로
        여전히 mem5의 age가 40으로 남아있다.

        벌크 연산 후 무언가 추가작업이 있다면 영속성 컨텍스트 초기화가 필요하지 않을지 검토해야한다.
         */
        List<Member> result = mRepo.findByUsername("mem5");
        Member member = result.get(0);
        assertThat(member.getAge()).isEqualTo(40);

        em.flush();
        em.clear();

        // 영속성 컨텍스트 초기화 후 다시 불러왔기 때문에 이제 41로 올바른 값이 조회됨
        result = mRepo.findByUsername("mem5");
        member = result.get(0);
        assertThat(member.getAge()).isEqualTo(41);

        // assert
        assertThat(affected).isEqualTo(3);
    }

    @Test
    void bulk2AgePlus() {
        // assign
        mRepo.save(Member.of("mem1", 10));
        mRepo.save(Member.of("mem2", 19));
        mRepo.save(Member.of("mem3", 20));
        mRepo.save(Member.of("mem4", 21));
        mRepo.save(Member.of("mem5", 40));

        // action
        // 참고로 JPQL이 나가기 전에 영속성 컨텍스트가 flush됨
        int affected = mRepo.bulk2AgePlus(20);

        /*
        bulk2AgePlus 는 옵션으로 수행 직후 영속성 컨텍스트를 초기화했으므로 바로 정상적인 값을 얻어옴
         */
        List<Member> result = mRepo.findByUsername("mem5");
        Member member = result.get(0);
        assertThat(member.getAge()).isEqualTo(41);


        // assert
        assertThat(affected).isEqualTo(3);
    }
}