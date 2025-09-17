package it.korea.jpa.entity.comp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="deptment")
@Getter
@Setter
public class DepartEntity {

    @Id
    private String deptId;
    private String deptName;
}
