package it.korea.jpa.service.gym;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.korea.jpa.dto.gym.MembersDTO;
import it.korea.jpa.entity.gym.LockEntity;
import it.korea.jpa.entity.gym.MembersEntity;
import it.korea.jpa.repository.gym.LockRepository;
import it.korea.jpa.repository.gym.MembersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MembersRepository memRepository;
    private final LockRepository lockRepository;

    /* 
    컨트롤러부터 서비스, 레파지토리까지 영속성 컨텍스트가 열려있다는 의미 >> 어디에서든 엔티티를 수정하면 트랜잭션으로 인해 업데이트 됨
    But, application.yml에서 open-in-view 을 false로 해놔서 영속성 컨텍스트가 열리지 않음 >> 서비스에 영속성 컨텍스트 열리게 하기위해 @Transactional 붙임
        -> 컨트롤러 ~ 서비스에 영속성 컨텍스트가 열림
    laze 정책..?
    */
    // jpa의 서비스에선 이거 무조건
    @Transactional
    public void newMembers(MembersDTO dto) throws Exception{
        // 아이디, 이름, 라커번호
        
        // Optional한테 물어봄 >> LockEntity 얘 있니? >> 있으면 그거 주고, 없으면 에러 내! or 괜찮아! (null 관리 가능)
        /**
         * Optional 은 특정 객체를 담는 컨테이너
         * 데이터베이스 또는 로직으로부터 얻은 객체가 비어 있거나,
         * 정상적이지 않을 때, 개발자가 처리할 수 있도록 만든 상자
         * null 로 인한 오류를 방지하기 위한 장치
         */
        // JPA로 호출된 객체라서 영속성 안에 존재
        Optional<LockEntity> lock = lockRepository.findById(dto.getLocNum());

        // 이렇게 해도 됨(선택사항)
        // 엔티티 꺼냈는데 없으면 에러 내렴
        // LockEntity lockEntity = lock.orElseThrow(() -> new RuntimeException("사물함이 없음")); 
        // LockEntity lockEntity = lock.orElseThrow(null);  // 없으면 null 줘!
        // if(lock.get().getUseYn().equals("Y")){
        //     throw new RuntimeException("사물함이 없거나 점유 중");
        // }

        // 너 있니?
        // get() --> entity를 꺼내는 거
        if(!lock.isPresent() || lock.get().getUseYn().equals("Y")){
            throw new RuntimeException("사물함이 없거나 점유 중");
        }

        // 사물한 Y로 변경
        // LockEntity lockEntity = new LockEntity();  >> 얘는 수정 안됨 >> 그냥 얘는 새로 선언된 아이
        LockEntity lockEntity = lock.get(); // 엔티티만 줘
        lockEntity.setUseYn("Y");  // 트랜잭션 안에서 Entity 변경 시 자동으로 update (id를 가지고 있어서)

        // @Transactional 있으면 엔티티 변경 시 자동 업데이트 됨
        // 업데이트는 save와 같음
        // lockRepository.save(lockEntity);

        // 멤버정보 업데이트 
        MembersEntity membersEntity = MembersDTO.to(dto);
        memRepository.save(membersEntity);

    }
}
