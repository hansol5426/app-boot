package it.korea.jpa.entity.comp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// 사원 엔티티
@Setter
@Getter
@Entity
@Table(name="employee")
public class EmployeeEntity {

    @Id
    private String emId;
    private String emName;

    // 부서 매핑(단방향)
    // LAZY : 연관된 데이터를 실제로 접근(호출)할 때 SELECT 쿼리를 실행해서 가져오는 방식
    //        >> 초기 로딩 시에는 연관 데이터를 불러오지 않으므로 성능이 향상됨
    //        >> 많은 양의 연관 데이터를 가진 경우, 꼭 필요한 순간에만 로딩되므로 효율적
    //        >> OneToMany or ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    // 설정을 안하면 조인 시 내가 설정한 컬럼이랑 상대방의 기본키가 기본으로 연결됨
    //dept_id >> employee테이블의 컬럼
    @JoinColumn(name="dept_id", nullable = false)
    private DepartEntity department;

    // emp가 나의 주인이다!
    // orphanRemoval : 고아 객체 삭제 -> 연관관계가 끊어진 자식 엔티티를 자동으로 삭제
    // cascade : 한쪽에다만 줘도 되는데, mappedBy 있는 쪽에다가 주는 게 좋음
    // optional : left join을 inner join으로 변경하여 출력하고 싶을 때 쓰는 가장 쉬운 방법
    @OneToOne(mappedBy = "emp",cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    // 단방향으로 맺어짐
    // @OneToOne(fetch = FetchType.EAGER)
    // @JoinColumn(name="em_id", referencedColumnName = "em_id")
    private EmCardEntity card;

    // 메인테이블 : 외래키를 가지고 있는테이블이 메인테이블?

    // 양방향 매핑의 이유
    // 1. 메인테이블의 외래키를 서브테이블이 가지고 있을 때
    // 2. 서로에게 영향을 주는 연결을 만들고 싶을 때 

}
