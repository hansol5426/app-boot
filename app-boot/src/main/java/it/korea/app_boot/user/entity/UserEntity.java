package it.korea.app_boot.user.entity;

import it.korea.app_boot.common.Entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="tb_users")
public class UserEntity extends BaseEntity{

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

    // 데이터가 많지 않아서 즉시 가져오는 걸로 함
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_role")
    private UserRoleEntity role;

}
