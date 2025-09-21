package it.korea.app_boot.board.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import it.korea.app_boot.board.dto.BoardSearchDTO;
import it.korea.app_boot.board.entity.BoardEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class BoardSearchSpecification implements Specification<BoardEntity>{

    private BoardSearchDTO searchDTO;

    public BoardSearchSpecification(BoardSearchDTO searchDTO){
        this.searchDTO = searchDTO;
    }

    @Override

    // root : 대상(where조건의 컬럼 대상) >> entity >> jpa가 만들어서 넣어줌(BoardRepository의 JpaSpecificationExecutor에 사용할 엔티티를 넣었기 때문에 )
    // query : sql 조작(아래 예시)
    // CriteriaBuilder : where 조건 >> and조건, or조건 만드는 애
    public Predicate toPredicate(Root<BoardEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        
        String likeText = "%" + searchDTO.getSchText() + "%";

        /*
         * query 파라미터는 sql을 조작할 수 있지만 잘 안씀
         * service단 또는 pageable 에서 이미 정렬 혹은 페이징 처리 하기 때문
         * 여기서 추가로 조작하면 복잡도 상승
         * 오류 시 유지보수가 어려워짐 
         */
        // 이렇게 쓸 수 있지만 잘 안씀 >> 외부에서 조작한 후에 dynamic 조건을 처리할 때 쓰기 때문에 여기서 사용하면 더 복잡함
        // query.distinct(true);
        // query.groupBy(root.get("title"));
        // query.orderBy(cb.desc(root.get("createDate")));

        // 하드코딩된 애를 비교할 때는 객체로 비교하는 것이 아닌 값을 찍어서 비교하는 것이 좋음
        // 왜? >> 객체의 비교대상이 null 일경우 오류가 나기 때문에 반대로 비교하여 null 방지 가능
        // searchDTO.getSchType().equals("title") >> nullPointException(NPE) 날 수 있음
        // "title".equals(searchDTO.getSchType()) >> 실무에선 이렇게! >> nullPointException(NPE) 막을 수 있음
        if("title".equals(searchDTO.getSchType())){
            predicates.add(cb.like(root.get("title"), likeText));  // title like %등록%  >> 이런식으로 생기는 거임
        }else if("writer".equals(searchDTO.getSchType())){
            predicates.add(cb.like(root.get("writer"), likeText));
        }

        return andToghther(predicates, cb);
    }

    private Predicate andToghther(List<Predicate> predicates, CriteriaBuilder cb){
        return cb.and(predicates.toArray(new Predicate[0]));
    }
    
}
