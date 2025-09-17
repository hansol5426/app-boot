package it.korea.jpa.entity.comp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="emp_cards")
@Getter
@Setter
public class EmCardEntity {

    @Id
    private String cardId;

    private int balance;

    // 한 개라서 EAGER
    // ALL : 모든 영속성(수정,삭제,등록 등)에 대해서 열어두겠다
    // 영속성을 열어두지 않으면 이 작업만 하고 끝남
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="em_id", nullable = false)
    EmployeeEntity emp;
}
