package it.korea.jpa.repository.comp;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.korea.jpa.dto.comp.EmployeeDTO;
import it.korea.jpa.dto.comp.EmployeeProjection;
import it.korea.jpa.entity.comp.EmployeeEntity;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity,String>{

    // fetch 조인 >> 페이징처리 X
    // service에서 findAll()을 이걸로 바꿔줘야 함 
    @Query("select e from EmployeeEntity e join fetch e.department join fetch e.card")
    List<EmployeeEntity> getEmployeeList();

    // graph 조인 >> 페이징처리 O
    // repository에서 제공하는 함수에만 가능(custom X)
    @EntityGraph(attributePaths = {"department", "card"})
    Page<EmployeeEntity> findAll(Pageable pageable);

    // native SQL 사용
    // 그냥 DTO는 못씀(매핑 X) => DTO랑 똑같은 인터페이스를 만들어야 함(프로젝션)
    @Query(value = """
                select e.em_id,
                       e.em_name,
                       d1.dept_name,
                       c1.balance
                from employee e
                    join emp_cards c1 on e.em_id = c1.em_id
                    join deptment d1 on e.dept_id = d1.dept_id 
            """,
           // 페이징 처리할때 nativeSQL 쓰려면 아래 두개 무조건!
           countQuery = "select count(*) from employee",
           nativeQuery = true)
           Page<EmployeeProjection> getEmployeeAllList(Pageable pageable);

}
