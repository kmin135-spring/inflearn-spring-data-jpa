package study.datajpa.entity;

import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.*;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString(of = {"id", "username", "age"})
public class Member extends JpaBaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public static Member of(String username, int age) {
        Member m = new Member();
        m.setUsername(username);
        m.setAge(age);
        return m;
    }

    public static Member of(String username, int age, Team team) {
        Member m = of(username, age);
        m.setTeam(team);
        return m;
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

    public void changeAge(int newAge) {
        setAge(newAge);
    }

    public void changeName(String newName) {
        setUsername(newName);
    }
}
