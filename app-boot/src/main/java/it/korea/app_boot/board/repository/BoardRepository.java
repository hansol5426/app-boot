package it.korea.app_boot.board.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.korea.app_boot.board.entity.BoardEntity;

// 유일하게 @ 생략가능 >> import 때매!
// JpaRepository<내가 사용할 entity의 타입 : entity클래스 , 내가 사용할 아이디 타입 : entity의 PK타입>
public interface BoardRepository extends JpaRepository<BoardEntity, Integer>, JpaSpecificationExecutor<BoardEntity>{
 
    /* 조건절 걸기
        첫번째 글자 하고 싶으면 : startwith
        마지막 글자 하고 싶으면 : endwith
        어디에라도 이런 글자 하고 싶으면 : containing
    */ 
    // title 검색
    Page<BoardEntity> findByTitleContaining(String title, Pageable pageable);
    // writer 검색
    Page<BoardEntity> findByWriterContaining(String writer, Pageable pageable);

    @Query(value = """
            select b 
            from BoardEntity b 
                left join fetch b.fileList 
            where b.brdId =:brdId
            """)
    Optional<BoardEntity> getBoard(@Param("brdId") int brdId);

}
