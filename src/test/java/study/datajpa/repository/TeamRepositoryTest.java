package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class TeamRepositoryTest {
    @Autowired
    private TeamRepository tRepo;
    @Autowired
    private EntityManager em;

    @Test
    void teamAuditing() throws InterruptedException {
        Team t = Team.of("team1");
        tRepo.save(t);

        Thread.sleep(1000);

        t.changeName("team2");

        em.flush();
        em.clear();

        System.out.println(t.getCreatedDate());
        System.out.println(t.getLastModifiedDate());
    }
}