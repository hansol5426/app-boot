package it.korea.app_boot.admin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.korea.app_boot.admin.dto.AdminUserDTO;
import it.korea.app_boot.admin.dto.AdminUserProjection;
import it.korea.app_boot.admin.dto.AdminUserSearchDTO;
import it.korea.app_boot.common.dto.PageVO;
import it.korea.app_boot.user.entity.UserEntity;
import it.korea.app_boot.user.entity.UserRoleEntity;
import it.korea.app_boot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Map<String,Object> getUserList(Pageable pageable) throws Exception{    
        Map<String,Object> resultMap = new HashMap<>();
        
        Page<UserEntity> pageList = 
                userRepository.findAll(pageable);

        List<AdminUserDTO> list = pageList.getContent()
                    .stream().map(AdminUserDTO::of).toList();

        PageVO pageVO = new PageVO();
        pageVO.setData(pageList.getNumber(), (int)pageList.getTotalElements());

        resultMap.put("total", pageList.getTotalElements());
        resultMap.put("content",list);
        resultMap.put("pageHTML", pageVO.pageHTML());
        resultMap.put("page", pageList.getNumber());

        return resultMap;
    }

    @Transactional
    public Map<String,Object> getUserList(Pageable pageable, AdminUserSearchDTO searchDTO ) throws Exception{    
        Map<String,Object> resultMap = new HashMap<>();
        
        Page<UserEntity> pageList = null;

        // isNotBlank : null 처리도 됨
        if(StringUtils.isNotBlank(searchDTO.getSearchText())){
            pageList = userRepository
                .findByUserIdContainingOrUserNameContaining(searchDTO.getSearchText(), searchDTO.getSearchText(), pageable);
        }else{
            pageList = userRepository.findAll(pageable);
        }

        List<AdminUserDTO> list = pageList.getContent()
                    .stream().map(AdminUserDTO::of).toList();

        PageVO pageVO = new PageVO();
        pageVO.setData(pageList.getNumber(), (int)pageList.getTotalElements());

        resultMap.put("total", pageList.getTotalElements());
        resultMap.put("content",list);
        resultMap.put("pageHTML", pageVO.pageHTML());
        resultMap.put("page", pageList.getNumber());

        return resultMap;
    }

    // 사용자 등록
    @Transactional
    public Map<String,Object> writeUser(AdminUserDTO dto) throws Exception{

        
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(dto.getUserId());

        // 비밀번호 암호화하여 등록
        String passwdEncode = passwordEncoder.encode(dto.getPasswd()); 
        userEntity.setPasswd(passwdEncode);

        userEntity.setUserName(dto.getUserName());
        userEntity.setGender(dto.getGender());
        userEntity.setBirth(dto.getBirth());
        userEntity.setPhone(dto.getPhone());
        userEntity.setEmail(dto.getEmail());
        userEntity.setAddr(dto.getAddr());
        userEntity.setAddrDetail(dto.getAddrDetail());
        
        UserRoleEntity roleEntity = new UserRoleEntity();
        roleEntity.setRoleId(dto.getUserRole());
        userEntity.setRole(roleEntity);

        userEntity.setUseYn(dto.getUseYn());
        userEntity.setDelYn("N");
        
        // db에 저장
        userRepository.save(userEntity);
        
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("resultCode", 200);
        resultMap.put("resultMsg", "OK");   

        return resultMap;
    }

    // 사용자 수정
    @Transactional
    public Map<String,Object> updateUser(AdminUserDTO dto) throws Exception{ 
        
        // 사용자ID로 기존 사용자 조회
        UserEntity userEntity = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // 수정할 데이터가 있을 경우에만 수정
        if(dto.getPasswd() != null) {
        String passwdEncode = passwordEncoder.encode(dto.getPasswd());
        userEntity.setPasswd(passwdEncode);
        }

        if(dto.getUserName() != null) userEntity.setUserName(dto.getUserName());
        if(dto.getGender() != null) userEntity.setGender(dto.getGender());
        if(dto.getBirth() != null) userEntity.setBirth(dto.getBirth());
        if(dto.getPhone() != null) userEntity.setPhone(dto.getPhone());
        if(dto.getEmail() != null) userEntity.setEmail(dto.getEmail());
        if(dto.getAddr() != null) userEntity.setAddr(dto.getAddr());
        if(dto.getAddrDetail() != null) userEntity.setAddrDetail(dto.getAddrDetail());
        
        if(dto.getUserRole() != null) {
            UserRoleEntity roleEntity = new UserRoleEntity();
            roleEntity.setRoleId(dto.getUserRole());
            userEntity.setRole(roleEntity);
        }
        
        if(dto.getUseYn() != null) userEntity.setUseYn(dto.getUseYn());
        
        // db에 저장
        userRepository.save(userEntity);    

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("resultCode", 200);
        resultMap.put("resultMsg", "OK");   

        return resultMap;

    }

    // 사용자 삭제
    // 실제 db삭제가 아닌 삭제여부만 Y로 변경
    @Transactional
    public Map<String,Object> deleteUser(String userId) throws Exception{

        // 사용자 조회
        UserEntity userEntity = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // 삭제여부 변경
        userEntity.setDelYn("Y");

        // db에 저장
        userRepository.save(userEntity);  

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("resultCode", 200);
        resultMap.put("resultMsg", "OK");   

        return resultMap;

    }

    // 상세정보 주기
    @Transactional
    public AdminUserDTO getUser(String userId) throws Exception{
        AdminUserProjection user = userRepository
                .getUserById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        return AdminUserDTO.of(user);
    }

}
