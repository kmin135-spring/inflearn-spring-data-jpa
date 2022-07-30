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