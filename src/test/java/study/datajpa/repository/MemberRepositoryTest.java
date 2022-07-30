package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

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
}