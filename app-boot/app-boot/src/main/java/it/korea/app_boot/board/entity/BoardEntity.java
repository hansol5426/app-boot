package it.korea.app_boot.board.entity;

import java.util.HashSet;
import java.util.Set;

import it.korea.app_boot.common.Entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// @Data면 getter, setter, toString이 생기는데 JPA의 entity 객체를 toString할때 순환참조 오류 발생함 >> 출력에 문제 생김 >> 위험 >> DTO 쓰는 이유 1
// => 그래서 따로 선언
// 스프링부트는 영속성 컨텍스트가 컨트롤러부터 레파지토리까지임 >> 중간에 엔티티를 수정하면 실제 테이블 데이터가 변경될 수 있음 >> 위험 >> DTO 쓰는 이유 2 
@Getter
@Setter
// 내가 Entity 다!
@Entity
// 이 테이블이랑 연결할거야!
@Table(name="board")
public class BoardEntity extends BaseEntity{
    // 내가 id 다!!
    @Id
    // AUTO_INCREMENT 일시 사용
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int brdId;
    private String title;
    private String contents;
    private String writer;
    private int readCount;
    private int likeCount;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    // @Fetch(FetchMode.SUBSELECT)  // 하이버네이트 전용 >> 데이터 적을때만 사용
    private Set<BoardFileEntity> fileList = new HashSet<>();


    public void addFiles(BoardFileEntity entity){
        if(fileList == null) this.fileList = new HashSet<>();
        entity.setBoard(this);
        fileList.add(entity);
    }
}
