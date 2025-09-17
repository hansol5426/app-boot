package it.korea.app_boot.board.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.korea.app_boot.board.dto.BoardDTO;
import it.korea.app_boot.board.dto.BoardSearchDTO;
import it.korea.app_boot.board.service.BoardJPAService;
import it.korea.app_boot.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;



// return이 view가 아닌 data
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class BoardAPIController {

    private final BoardService service;

    private final BoardJPAService jpaService;

    @GetMapping("/board/list")
    public ResponseEntity<Map<String, Object>> getBoardSearchDTO(BoardSearchDTO searchDTO){

        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        try{

            resultMap = service.getBoardList(searchDTO);

        }catch(Exception e){
            // 500 에러
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
        }

        // HttpServletResponse + HttpStatus 결합 객체
        // 서버와 클라이언트가 따로 있을 때 결과와 상태를 보내는 것 >> 어떤 문제때문에 에러가 발생했는지 알 수 있음
        return new ResponseEntity<>(resultMap,status);
    }

    @GetMapping("/board/data")
    public  ResponseEntity<Map<String, Object>> getBoardData(BoardSearchDTO searchDTO) {

        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;
        log.info("============ 게시판 데이터 가져오기 ==============");

        List<Sort.Order> sorts = new ArrayList<>();
        String[] sidxs = searchDTO.getSidx().split(",");
        String[] sords = searchDTO.getSord().split(",");

        for(int i = 0; i < sidxs.length; i++){
            if(sords[i].equals("asc")){
                sorts.add(new Sort.Order(Sort.Direction.ASC, sidxs[i]));
            }else{
                sorts.add(new Sort.Order(Sort.Direction.DESC, sidxs[i]));
            }
        }

        // 현재 페이지, 가져올 개수, order by 객체 > 페이지 객체 만들기
        Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), Sort.by(sorts));

        try{

            resultMap = jpaService.getBoardList(searchDTO, pageable);

        }catch(Exception e){
            // 500 에러
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();

        }

        // HttpServletResponse + HttpStatus 결합 객체
        return new ResponseEntity<>(resultMap,status);
    
    }

    @GetMapping("/board/{brdId}")
    public ResponseEntity<Map<String, Object>> getBoard(@PathVariable("brdId") int brdId) throws Exception{

        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        resultMap = jpaService.getBoard(brdId);

        return new ResponseEntity<>(resultMap,status);
    }   
    
    // 게시글 작성
    @PostMapping("/board")
    public ResponseEntity<Map<String, Object>> writeBoard(@Valid @ModelAttribute BoardDTO.Request request) throws Exception{

        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        resultMap = jpaService.writeBoard(request);

        return new ResponseEntity<>(resultMap,status);
    }

    // 게시글 수정
    @PutMapping("/board")
    public ResponseEntity<Map<String, Object>> updateBoard(@Valid @ModelAttribute BoardDTO.Request request) throws Exception{

        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        resultMap = jpaService.updateBoard(request);

        return new ResponseEntity<>(resultMap,status);
    }

    // 게시글 삭제
    @DeleteMapping("/board/{brdId}")
    public ResponseEntity<Map<String, Object>> deleteBoard(@PathVariable("brdId") int brdId) throws Exception{

        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;
        resultMap = jpaService.deleteBoard(brdId);
        return new ResponseEntity<>(resultMap,status);
    }

    // 첨부파일 다운로드
    @GetMapping("/board/file/{bfId}")
    public ResponseEntity<Resource> downFile(@PathVariable("bfId") int bfId) throws Exception{
        return jpaService.downLoadFile(bfId);
    }

    // 파일 삭제
    @DeleteMapping("/board/file/{bfId}")
    public ResponseEntity<Map<String, Object>> delFile(@PathVariable("bfId") int bfId) throws Exception{
        
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;
        resultMap = jpaService.delFile(bfId);
        return new ResponseEntity<>(resultMap,status);
    }

}
