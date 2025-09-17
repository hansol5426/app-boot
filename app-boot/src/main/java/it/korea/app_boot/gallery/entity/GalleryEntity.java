package it.korea.app_boot.gallery.entity;

import it.korea.app_boot.common.Entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="gallery")
public class GalleryEntity extends BaseEntity{

    /*  
        * pk의 자동증가가 보장이 안되서 jpa에서는 자동증가가 안됨
        * 오라클에서 순서에 대한 보장이 안됨 -> 대용량 배치 사용 불가
        * -> 시퀀스를 테이블에서 꺼내서 쓰든가 
        * id값 사용자가 만들어서 쓰던가 
    */
    
    @Id
    private String nums;
    
    private String title;
    private String writer;
    private String fileName;
    private String storedName;
    private String filePath;
    private String fileThumbName;
}
