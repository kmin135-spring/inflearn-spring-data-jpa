package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {
    @Autowired
    private MemberJpaRepository mRepo;

    @Test
    void testMember() {
        Member m = Member.of("memberA", 30);
        Member saved = mRepo.save(m);

        Member found = mRepo.find(saved.getId());

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
    void basicUpdate() {
        Member um = Member.of("beforeMember", 10);
        mRepo.save(um);

        um.changeAge(20);
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

}