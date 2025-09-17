package it.korea.jpa.service.comp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.korea.jpa.dto.comp.EmployeeDTO;
import it.korea.jpa.dto.comp.EmployeeProjection;
import it.korea.jpa.entity.comp.DepartEntity;
import it.korea.jpa.entity.comp.EmCardEntity;
import it.korea.jpa.entity.comp.EmployeeEntity;
import it.korea.jpa.repository.comp.DepartmentRepository;
import it.korea.jpa.repository.comp.EmployeeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    // 패치조인
    @Transactional
    public List<EmployeeDTO> getEmployeeList(){
        List<EmployeeEntity> list = employeeRepository.getEmployeeList();
        List<EmployeeDTO> empList = list.stream().map(EmployeeDTO::of).toList();
        return empList;
    }

    // 그래프조인
    @Transactional
    public List<EmployeeDTO> getEmployeePageList(Pageable pageable){
        Page<EmployeeEntity> list = employeeRepository.findAll(pageable);
        List<EmployeeDTO> empList = list.stream().map(EmployeeDTO::of).toList();
        return empList;
    }

    // 네이티브sql
    @Transactional
    public List<EmployeeDTO> getEmployeePageList2(Pageable pageable){
        Page<EmployeeProjection> list = employeeRepository.getEmployeeAllList(pageable);
        // 서비스에서 기능 호출 후 DTO 또는 Entity에 옮겨 담는 과정
         List<EmployeeDTO>empList = 
                          list.getContent().stream().map(obj -> {
            return EmployeeDTO.builder()
                              .emId(obj.getEmId())
                              .emName(obj.getEmName())
                              .deptName(obj.getDeptName())
                              .profitAccount(obj.getBalance())                                  
                              .build();
         }).toList();


        return empList;
    }

    @Transactional
    public Map<String,Object> addNewEmployee(EmployeeDTO dto) throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        DepartEntity depart = 
            departmentRepository.findById(dto.getDeptId())
            .orElseThrow(()-> new RuntimeException("부서정보가 없음"));

            EmployeeEntity em = new EmployeeEntity();
            em.setDepartment(depart);
            em.setEmId(dto.getEmId());
            em.setEmName(dto.getEmName());

            // 카드 만들기
            EmCardEntity card = new EmCardEntity();
            // 카드 아이디 랜덤 생성
            String cardId = "ca" + UUID.randomUUID().toString().replaceAll("-", "").substring(0,6);
            card.setCardId(cardId);
            card.setBalance(200000);

            // 이게 중요!
            // 테이블 구조가 양방향이지만 JPA는 테이블 구조를 클래스, 즉 객체로 표현함
            // 객체는 실제로 독립구조이기 때문에 서로 양방향이 될 수 없음
            // 그래서 각자의 정보를 양쪽에 대입해줌으로써 서로를 바라보는 양방향 구조를 흉내낼 수 있음 -> mappedBy
            
            // 양방향구조는 한정적으로 사용하는 것이 좋음 >> 양방향은 객체의 연관관계의 복잡성을 높이는데, 객체지향에선 연관관계의 복잡성을 낮추는 것을 목표로 함
            // 그래서 기본적으로는 단방향임
            card.setEmp(em);   // 카드 -> 직원
            em.setCard(card);  // 직원 -> 카드
            
            // 카드 정보 + 사용자 정보 저장
            employeeRepository.save(em);

            resultMap.put("resultCode", 200);

            return resultMap;
    }

    public int deleteEmployee(String emId) throws Exception{
        int result = 1;

        EmployeeEntity em = employeeRepository
            .findById(emId)
            .orElseThrow(()-> new RuntimeException("사용자 없음"));

        //삭제
        employeeRepository.delete(em);

        return result;
    }

}
