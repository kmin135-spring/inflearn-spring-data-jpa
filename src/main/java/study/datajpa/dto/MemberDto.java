package study.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import study.datajpa.entity.Member;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String username;
    private String teamName;

    /**
     * DTO는 엔티티의 세부구현을 알아도 된다.
     * DTO에 엔티티를 파라미터로 받는 정적 메서드 팩토리 구현
     * */
    public static MemberDto of(Member member) {
        MemberDto md = new MemberDto();
        md.setId(member.getId());
        md.setUsername(member.getUsername());
        md.setTeamName(null);
        return md;
    }
}
