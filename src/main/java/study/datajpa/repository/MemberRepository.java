package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    /**
     * 페이징
     * 반환타입이 Page면 카운트 쿼리를 자동으로 날려준다.
     * */
    Page<Member> findByAge(int age, Pageable pageable);

    /**
     * 아래와 같이 드리븐 테이블만으로 카운트가 결정되는 쿼리일때도 (예제는 left join이고 where도 영향을 안 주니까)
     * 기본 카운트 쿼리는 동일한 join 을 가져간다.
     * 이에 따라 조인 구조나 데이터 양에 따라 카운트쿼리에 불필요한 부하가 걸릴 수 있다.
     *
     * 이 경우 카운트 결정에 필요한 최적화된 카운트 쿼리를 직접 명시할 수 있다.
     * 성능이 문제되는 시점에 분리를 검토하자.
     */
    @Query(value = "select m from Member m left join m.team t",
        countQuery = "select count(m) from Member m")
    Page<Member> findCustomCountByAge(int age, Pageable pageable);

    /**
     * 슬라이싱
     * 페이징과 달리 카운트 쿼리가 나가지않는다.
     * 대신 원래 요청한 limit보다 +1 해서 요청한다.
     * 주로 모바일용 페이지에서 전통적인 페이징 대신 "더 보기" 를 이용해서 추가 데이터를 불러올 때 유용하다.
     *
     * 카운트 쿼리가 없어서 전체페이지 등을 계산할 수 없으므로 해당 데이터는 제공하지 않는다.
     *
     */
    Slice<Member> findSliceByAge(int age, PageRequest pageable);

    /** limit, sort를 위해서만 PageRequest를 사용하고 반환은 리스트로 받아도 됨 */
    List<Member> findListByAge(int age, PageRequest pageable);
}
