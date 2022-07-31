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