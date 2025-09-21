package it.korea.app_boot.user.entity;

import it.korea.app_boot.common.Entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="user_role")
public class UserRoleEntity extends BaseEntity {

    @Id
    private String roleId;

    private String roleName;
    @Column(name="use_yn",length = 1, columnDefinition = "char(1)")
    private String useYn;
}
