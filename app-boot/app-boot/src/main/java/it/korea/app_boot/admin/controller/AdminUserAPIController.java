package it.korea.app_boot.admin.controller;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.korea.app_boot.admin.dto.AdminUserDTO;
import it.korea.app_boot.admin.dto.AdminUserSearchDTO;
import it.korea.app_boot.admin.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AdminUserAPIController {

    private final AdminUserService adminUserService;

    @GetMapping("/admin/user")
    public ResponseEntity<Map<String,Object>> getUserList(
            @PageableDefault(page = 0, size = 10, sort = "createDate", direction = Direction.DESC) Pageable pageable,
                             AdminUserSearchDTO searchDTO) throws Exception{      
                                  
        Map<String,Object> resultMap = adminUserService.getUserList(pageable, searchDTO);
        
        return new ResponseEntity<>(resultMap,HttpStatus.OK);
    } 

    // 사용자 등록
    @PostMapping("/admin/user")
    public ResponseEntity<Map<String,Object>> writeUser(@Valid @ModelAttribute AdminUserDTO dto) throws Exception{      
                                  
        Map<String,Object> resultMap = adminUserService.writeUser(dto);
        
        return new ResponseEntity<>(resultMap,HttpStatus.OK);
    } 
    
    // 사용자 수정
    @PutMapping("/admin/user")
    public ResponseEntity<Map<String,Object>> updateUser(@RequestBody AdminUserDTO dto) throws Exception{      
                                  
        Map<String,Object> resultMap = adminUserService.updateUser(dto);
        
        return new ResponseEntity<>(resultMap,HttpStatus.OK);
    } 

    // 사용자 삭제
    @DeleteMapping("/admin/user")
    public ResponseEntity<Map<String,Object>> deleteUser(@RequestParam("userId") String userId) throws Exception{      
                                  
        Map<String,Object> resultMap = adminUserService.deleteUser(userId);
        
        return new ResponseEntity<>(resultMap,HttpStatus.OK);
    } 

    
    
}
