package it.korea.jpa.repository.gym;

import org.springframework.data.jpa.repository.JpaRepository;

import it.korea.jpa.entity.gym.MembersEntity;

public interface MembersRepository extends JpaRepository<MembersEntity, String>{

    
}
