# 개요

* 김영한 스프링 데이터 JPA 실전

# 메모

* 쿼리 생성 규칙
  * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
* 메서드 이름을 통한 자동생성은 파라미터 2개 정도까지는 쓸만하다
  * 그 이상은 메서드 이름이 너무 길어지므로 `@Query` 로 직접 JPQL을 사용하는 등의 방법으로 대체
* 오타가 나면 애플리케이션 로딩 시점에 바로 파악이 가능

---

* NamedQuery 는 엔티티에 미리 JPQL을 정의해두고 호출하는 방식
* data-jpa 에서는 `@Query(name = "Member.findByUsername)` 와 같이 사용 가능
  * 추가로 관례에 따라 메서드 이름과 NamedQuery와 이름이 같으면 `@Query` 생략 가능
* 근데 NamedQuery 자체가 실무에서 잘 안 씀
  * 엔티티에 쿼리가 정의된다는 것부터가 장황함
  * 장점은 애플리케이션 로딩 시점에 에러를 띄워주는 것 (정적 쿼리이기 때문에 가능)
* data-jpa는 `@Query` 로 JPQL을 사용할 수 있고 NamedQuery의 장점인 로딩시점 JPQL 유효성 검사도 해줌 

---

* data-jpa 도 동적쿼리는 작성이 어렵다.
* 동적쿼리는 QueryDSL 을 쓰자.

---

* 페이징의 시작이 0인 것에 주의
* 소팅은 쿼리가 복잡해지면 data-jpa 기본 제공 기능으로 커버가 어려울 수 있으므로 이런 경우 과감히 직접 JPQL을 작성하자
* 기본 생성 카운트쿼리가 무거워지면 직접 작성하자

---

* JPQL DML 이든 mybatis, jdbcTemplate 이든 영속성 컨텍스트가 인식하지 못 하는 변경 작업을 같은 트랜잭션 내에서 섞어서 사용할 때는
  * 영속성 컨텍스트 초기화가 필요하지 않을지 세심히 검토해봐야함

---

* fetch join은 연관관계를 한 번에 조인해주는 JPA 기술
* 간단한 fetch join일 때는 `@EntityGraph` 가 편하고
* 좀 복잡하다 싶으면 그냥 JPQL로 직접 짜는걸 권장
* NamedQuery와 비슷하게 NamedEntityGraph 도 있으니 필요하면 참고

---

* JPA Hint는 순수 SQL hint가 아니고 JPA 자체에 대한 힌트임
* 성능테스트 후 의미있는 향상이 있을때만 적용하는 것을 권장
* 그 전에 애초에 redis 등의 캐시시스템 등을 도입해야하는 것은 아닌지도 검토

---

* Lock도 애노테이션으로 간단히 설정 가능
* 세부적인 내용은 교재를 참고하자
* 실시간 대규모 트래픽 : 비관적 락은 쓰지말자(데드락 등 발생), 낙관적 락이나 락을 걸지 않고 처리할 방법을 검토
* 소규모지만 일관성이 중요한 서비스 : 돈을 계산한다던가 할 경우. 이럴 때는 비관적 락을 쓰는 것도 좋다.

---

* 사용자 정의 레포지토리 기능으로 레포지토리를 커스텀할 수 있음
* 간단한 건 기본 data-jpa 로 해결이 되겠지만
* 동적쿼리나 복잡한 쿼리를 작성할 때는 사용자 정의 리포지토리를 적용해서 커스텀하자
  * QueryDSL 커스텀할 때 용이함
* 하지만 사용자 정의 리포지토리를 꼭 써야하는건 아님.
  * data-jpa용 리포지토리는 간단한 용도로만 쓰고 (자동생성메서드, 간단한 JPQL 정도)
  * 복잡한 쿼리들은 그냥 일반적인 리포지토리를 생성해서 써도 됨
  * 오히려 유지보수 측면에서는 이게 나을 수 있음
* CQRS의 개념에서 커맨드와 쿼리 모델을 나누듯이
  * 간단한것과 복잡한것을 분리하는 설계를 고려해야함 -> 하나의 도구에 매몰되서 문제를 해결하려고 하지 말자

---

* 등록일, 등록자, 수정일, 수정자와 같은 공통 Auditing 기능은 실무에서도 많이 사용
* 순수 JPA는 `@PrePersist, @PreUpdate, @PostPersist, @PostUpdate` 등을 사용
* data-jpa는 `@CreatedDate, @LastModifiedDate` 와 같이 더 편리한 기능 제공
  * `@EnableJpaAuditing, @EntityListeners(AuditingEntityListener.class)` 등의 차이점에 주의
* 수정일, 수정자도 초기값을 null로 두지 말고 등록일, 등록자와 같은 값으로 하자
  * null이면 쿼리를 날릴 때 null인 경우를 고려해야하기 때문에 복잡성만 높아지기 때문
* `@EntityListeners(AuditingEntityListener.class)` 는 `META-INF/orm.xml` 에 전체 적용하는 것도 가능함
* 대부분의 테이블은 등록일, 수정일은 필수이므로 필수로 가져가는게 좋음
  * 반면 등록자, 수정자 정보는 필요 없는 경우도 존재함
  * 이런 경우 예제처럼 BaseEntity, BaseTimeEntity 를 상속구조로 만들어두고 필요에 따라 상속해서 사용할 수 있음

---

* Specification 은 JPQL Criteria 를 감싼 기술로 마찬가지로 실무에서 쓰기에는 복잡함
* Example 은 검색데이터를 채운 도메인 객체를 파라미터로 넘겨 쿼리해주는 기술
  * 장점 : 동적 쿼리를 편리하게 사용할 수 있고 도메인 객체를 그대로 사용할 수 있는 등
  * 단점 : INNER JOIN만 가능하고 OUTER JOIN이 안 됨. 매칭조건도 단순한것만 지원. 중첩 제약조건도 안 됨
  * 단점이 더 커서 이것도 실무에서 쓰기에는 모호함.
  * 왜냐하면 단점에 거론한 조건이 1개라도 필요하게되면 구현을 다 뜯어고쳐야함.
* Projections 는 엔티티 대신 DTO 로 조회할 때 사용
  * 쉽게 말하면 select 문에서 조회하는 필드들 매핑
  * 이건 그래도 루트엔티티에만 쓸 때는 그럭저럭 쓸만하지만 조인을 하기 시작하면 최적화가 안 됨
* 결론 : Specification, Example, Projections 모두 QueryDSL로 더 깔끔하게 처리가 가능함

---

* NativeQuery 는 반드시 필요한 상황이 아니면 쓰지말자
* NativeQuery 는 제약이 많음 (반환 타입, 소팅, ...)
* Projection 이 도입됨에 따라 그래도 DTO로 받기 편해졌고 Page, Pageable 도 사용 가능함
  * 간단한 정적 쿼리에 한해서라면 쓸만할지도...? 
* 이걸 써야할 상황이면 별도의 레포지토리를 만들어 jdbcTemplate, mybatis 등의 기술을 적용하자