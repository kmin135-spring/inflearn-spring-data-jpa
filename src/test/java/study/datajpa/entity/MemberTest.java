package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {
    @Autowired
    EntityManager em;

    @Test
    void testEntity() {
        Team t1 = Team.of("teamA");
        Team t2 = Team.of("teamB");
        em.persist(t1);
        em.persist(t2);

        Member m1 = Member.of("member1", 10, t1);
        Member m2 = Member.of("member2", 20, t1);
        Member m3 = Member.of("member3", 30, t2);
        Member m4 = Member.of("member4", 40, t2);

        em.persist(m1);
        em.persist(m2);
        em.persist(m3);
        em.persist(m4);

        // 영속성 컨텍스트 강제반영 및 초기화
        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

        for (Member member : members) {
            System.out.println("member : " + member);
            System.out.println("-> member.team : " + member.getTeam());

        }
    }
}