package it.korea.jpa.entity.gym;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="tb_lock")
@Getter
@Setter
public class LockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int locNum;

    private int numbers;
    // @Column(name="use_yn", length = 1, columnDefinition = "char(1)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private String useYn;

}
