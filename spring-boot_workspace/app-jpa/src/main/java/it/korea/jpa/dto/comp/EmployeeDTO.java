package it.korea.jpa.dto.comp;

import it.korea.jpa.entity.comp.EmployeeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeDTO {

    private String emId;
    private String emName;
    private String deptId;
    private String deptName;
    private int profitAccount;

    public static EmployeeDTO of(EmployeeEntity entity){
        return EmployeeDTO.builder()
                          .emId(entity.getEmId())
                          .emName(entity.getEmName())
                          // 이거 부를 때 LAZY 불려짐
                          .deptId(entity.getDepartment().getDeptId())
                          .deptName(entity.getDepartment().getDeptName())
                          .profitAccount(entity.getCard().getBalance())
                          .build();
    }

}
