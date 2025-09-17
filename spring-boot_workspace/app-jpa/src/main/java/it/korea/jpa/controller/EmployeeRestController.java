package it.korea.jpa.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.korea.jpa.dto.comp.EmployeeDTO;
import it.korea.jpa.service.comp.EmployeeService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/emp")
public class EmployeeRestController {

    private final EmployeeService employeeService;

    // 패치조인
    @GetMapping("/list")
    public List<EmployeeDTO> getList(){
        return employeeService.getEmployeeList();
    }

    // 그래프조인
    @GetMapping("/page/list")
    public List<EmployeeDTO> getPageList(@PageableDefault(page = 0,
                                            size = 10, sort = "emId", direction = Sort.Direction.DESC)
                                            Pageable pageable){
        return employeeService.getEmployeePageList(pageable);
    }

    // 네이티브sql
    @GetMapping("/page/list2")
    public List<EmployeeDTO> getPageList2(@PageableDefault(page = 0,
                                            size = 10, sort = "em_id", direction = Sort.Direction.DESC)
                                            Pageable pageable){
        return employeeService.getEmployeePageList2(pageable);
    }


    @PostMapping(value ="", 
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,Object>> addNewEmployee(@RequestBody EmployeeDTO dto){
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        try{
            resultMap = employeeService.addNewEmployee(dto);

        }catch(Exception e){
            resultMap.put("resultCode", status.value());
            e.printStackTrace();
        }

        return new ResponseEntity<>(resultMap,status);
    }

    @DeleteMapping(value = "/{emId}")
    public ResponseEntity<Map<String,Object>> addNewEmployee(@PathVariable("emId") String emId){

        Map<String, Object>  resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        try{
            int result = employeeService.deleteEmployee(emId);

            if(result < 0){
                throw new Exception("삭제 실패");
            }

        }catch(Exception e){
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();

        // resultCode 남기는용
        // 만약 finally 없으면 try 블록에서 예외가 나면 resultMap.put()이 실행되지 않을 수도 있음
        //  → 응답 JSON에서 "resultCode"가 빠질 수 있음 → 프론트가 응답 해석 못 함
        }finally{
            resultMap.put("resultCode", status.value());
        }

        return new ResponseEntity<>(resultMap, status);

    }

}
