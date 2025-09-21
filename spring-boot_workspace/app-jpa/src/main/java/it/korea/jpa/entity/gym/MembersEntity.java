package it.korea.jpa.entity.gym;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="members")
@Getter
@Setter
public class MembersEntity {

    @Id
    private String memId;

    private String memName;
    private int locNum;

}
