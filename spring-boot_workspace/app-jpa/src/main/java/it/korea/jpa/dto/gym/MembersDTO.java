package it.korea.jpa.dto.gym;

import it.korea.jpa.entity.gym.MembersEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MembersDTO {

    private String memId;
    private String memName;
    private int locNum;

    // 엔티티 -> DTO 변환 메서드
    public static MembersDTO of(MembersEntity entity){
        return MembersDTO.builder()
                         .memId(entity.getMemId())
                         .memName(entity.getMemName())
                         .locNum(entity.getLocNum())
                         .build();
    }

    // DTO -> 엔티티 변환 메서드
    public static MembersEntity to(MembersDTO dto){
        MembersEntity entity = new MembersEntity();

        entity.setMemId(dto.memId);
        entity.setMemName(dto.getMemName());
        entity.setLocNum(dto.getLocNum());

        return entity;
    }


}
