package study.datajpa.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Member {
    @Id
    @GeneratedValue
    private Long id;
    private String username;

    public static Member of(String username) {
        Member m = new Member();
        m.setUsername(username);
        return m;
    }
}
