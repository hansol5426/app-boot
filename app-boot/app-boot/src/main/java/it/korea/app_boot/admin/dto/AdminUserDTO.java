package it.korea.app_boot.admin.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import it.korea.app_boot.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminUserDTO {

    @Id
    private String userId;

    private String passwd;
    private String userName;
    private String birth;
    private String gender;
    private String phone;
    private String email;
    private String addr;
    private String addrDetail;
    @Column(name="use_yn",length = 1, columnDefinition = "char(1)")
    private String useYn;
    @Column(name="del_yn",length = 1, columnDefinition = "char(1)")
    private String delYn;
    private String userRole;
    private String roleName;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;

    // entity를 dto로
    public static AdminUserDTO of(UserEntity entity){
        return AdminUserDTO.builder()
                           .userId(entity.getUserId())
                           .userName(entity.getUserName())
                           .birth(entity.getBirth())
                           .gender(entity.getGender())
                           .phone(entity.getPhone())
                           .email(entity.getEmail())
                           .addr(entity.getAddr())
                           .addrDetail(entity.getAddrDetail())
                           .useYn(entity.getUseYn())
                           .delYn(entity.getDelYn())
                           .createDate(entity.getCreateDate())
                           .updateDate(entity.getUpdateDate())
                           .userRole(entity.getRole().getRoleId())
                           .roleName(entity.getRole().getRoleName())
                           .build();
    }

     public static AdminUserDTO of(AdminUserProjection entity){
        return AdminUserDTO.builder()
                           .userId(entity.getUserId())
                           .userName(entity.getUserName())
                           .birth(entity.getBirth())
                           .gender(entity.getGender())
                           .phone(entity.getPhone())
                           .email(entity.getEmail())
                           .addr(entity.getAddr())
                           .addrDetail(entity.getAddrDetail())
                           .useYn(entity.getUseYn())
                           .delYn(entity.getDelYn())
                           .createDate(entity.getCreateDate())
                           .updateDate(entity.getUpdateDate())
                           .userRole(entity.getRoleId())
                           .roleName(entity.getRoleName())
                           .build();
    }

    // dto를 entity로
    public static UserEntity to(AdminUserDTO dto){
        UserEntity entity = new UserEntity();

        entity.setUserId(dto.getUserId());
        entity.setPasswd(dto.getPasswd());
        entity.setUserName(dto.getUserName());
        entity.setBirth(dto.getBirth());
        entity.setGender(dto.getGender());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setAddr(dto.getAddr());
        entity.setAddrDetail(dto.getAddrDetail());

        return entity;
    }

}
