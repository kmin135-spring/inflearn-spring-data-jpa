package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * 사용자 정의 레포지터리 제약조건
 * - 대상 인터페이스의 이름으로 시작하고 Impl 로 끝나야함
 * - config로 postfix를 바꿀 수 있긴한데 그냥 관례를 쓰자
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
