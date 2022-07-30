package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /*
    * 파라미터가 2개 이하 정도로 적을 때는 자동생성 방식도 유용함
    * 그 이상은 이름이 너무 복잡해져서 사용이 어려움
    * */
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /*
    * 조건이 복잡해지면 이 방식을 사용하자
    * JPA의 @NamedQuery 처럼 애플리케이션 로딩시점에 문법체크도 해준다.
    * */
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernames();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMembersDto();

    /** 컬렉션을 파라미터로한 in 조건도 쉽게 가능 */
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    /*
    반환 타입을 유연하게 사용할 수 있다
     */
    List<Member> findByUsername(String username);
    Member findOneByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);
}
