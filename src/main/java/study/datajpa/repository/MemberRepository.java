package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

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

    /* ****************************************************************
     * 반환타입
     * ****************************************************************/

    /*
    반환 타입을 유연하게 사용할 수 있다
     */
    List<Member> findByUsername(String username);
    Member findOneByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);

    /* ****************************************************************
     * 페이징
     * ****************************************************************/

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


    /* ****************************************************************
     * bulk update
     * ****************************************************************/

    /** Modifying 이 있어야 dml 쿼리가 나감 */
    @Modifying
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    /** 수행 후 영속성 컨텍스트 flush, clear 여부를 결정할 수 있음 */
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulk2AgePlus(@Param("age") int age);

    /* ****************************************************************
     * EntityGraph
     * ****************************************************************/

    // fetch join을 직접 작성하는 방법
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // @EntityGraph 사용 (이것도 결국 fetch join 임)
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // 직접 작성한 원본 JQPL 에 EntityGraph 로 fetch join
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 자동생성 메서드에도 적용 가능
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    /**
     * 변경감지 등의 기능을 포기하는 대신 읽기 성능을 약간 향상시킬 수 있음
     * 하지만 대부분의 경우 이 정도의 튜닝은 실질적으로 별 의미없는 경우가 많음
     * 부하가 큰 일부 포인트에 적용을 검토할 수 있으나 실제 성능테스트로 의미있는 향상이 있는지 확인해야함
     * 게다가 읽기 튜닝이 필요하면 redis 등의 캐시를 적용할 것이므로 더욱더 이런 튜닝이 필요한 상황은 적음
    */
    @QueryHints(value = @QueryHint(name="org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    /**
     * JPA가 제공하는 Lock 을 걸 수 있다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    List<UsernameOnlyDto> findProjectionsByUsername(@Param("username") String username);

    <T> List<T> findProjectionsByUsername(@Param("username") String username, Class<T> typ);

    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value = "select m.member_id as id, m.username, t.name teamName from member m left join team t",
            countQuery = "select count(*) from member", nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
