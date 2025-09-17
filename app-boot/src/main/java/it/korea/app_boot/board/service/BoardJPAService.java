package it.korea.app_boot.board.service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import it.korea.app_boot.board.dto.BoardDTO;
import it.korea.app_boot.board.dto.BoardFileDTO;
import it.korea.app_boot.board.dto.BoardSearchDTO;
import it.korea.app_boot.board.entity.BoardEntity;
import it.korea.app_boot.board.entity.BoardFileEntity;
import it.korea.app_boot.board.repository.BoardFileRepository;
import it.korea.app_boot.board.repository.BoardRepository;
import it.korea.app_boot.board.repository.BoardSearchSpecification;
import it.korea.app_boot.common.files.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardJPAService {

    @Value("${server.file.upload.path}")
    private String filePath;

    private final BoardRepository boardRepository;
    private final BoardFileRepository fileRepository;
    private final FileUtils fileUtils;

    public Map<String, Object> getBoardList(BoardSearchDTO searchDTO, Pageable pageable) throws Exception{
        
        Map<String, Object> resultMap = new HashMap<>();
        
        // findAll() -> select * from board; 랑 같은거
        Page<BoardEntity> pageObj = null;

        // search 검색 비교
        if(!StringUtils.isBlank(searchDTO.getSchType()) &&
                !StringUtils.isBlank(searchDTO.getSchText())){

            // 조건이 명확하고 분기가 많지 않을때는 요것도 나쁘지 않음
            // if(searchDTO.getSchType().equals("title")){
            //     pageObj = boardRepository.findByTitleContaining(searchDTO.getSchText(), pageable);
                        
            // }else if (searchDTO.getSchType().equals("writer")) {
            //     pageObj = boardRepository.findByWriterContaining(searchDTO.getSchText(), pageable);    
            // }

            // findBy절 안써도 가능한 코드 >> 조건과 분기가 많아서 만들어서 써야할 때 요게 좋음
            // BoardRepository에 JpaSpecificationExecutor 추가해줘야 아래 코드 가능
            BoardSearchSpecification searchSpecification = new BoardSearchSpecification(searchDTO);
            pageObj = boardRepository.findAll(searchSpecification, pageable);
            

        }else{
            pageObj = boardRepository.findAll(pageable);
        }

        // List of Entity ===> List of DTO
        // toList >> 불변객체 출력(리스트 수정이 안됨)
        // List<BoardDTO.Response> list = pageObj.getContent().stream().map(BoardDTO.Response::of).toList();
        List<BoardDTO.Response> list = pageObj.getContent().stream().map(BoardDTO.Response::of).collect(Collectors.toList());  // 가변 객체
        
        resultMap.put("total", pageObj.getTotalElements());
        resultMap.put("content",list);

        return resultMap;
    }

    @Transactional
    public Map<String,Object> getBoard(int brdId) throws Exception{
        Map<String, Object> resultMap = new HashMap<>();

        BoardEntity entity = boardRepository.getBoard(brdId).
            orElseThrow(()-> new RuntimeException("게시글 없음"));

        BoardDTO.Detail detail = BoardDTO.Detail.of(entity);
        
        resultMap.put("vo",detail);

        return resultMap;
    }

    @Transactional
    public Map<String, Object> writeBoard(BoardDTO.Request request) throws Exception{
        Map<String,Object> resultMap = new HashMap<>();

        // 물리적으로 저장
        Map<String, Object> fileMap = fileUtils.uploadFiles(request.getFile(), filePath);
        BoardEntity entity = new BoardEntity();
        entity.setTitle(request.getTitle());
        entity.setContents(request.getContents());
        entity.setWriter("admin");


        if(fileMap != null){
            BoardFileEntity fileEntity = new BoardFileEntity();
            fileEntity.setFileName(fileMap.get("fileName").toString());
            fileEntity.setStoredName(fileMap.get("storedFileName").toString());
            fileEntity.setFilePath(fileMap.get("filePath").toString());
            fileEntity.setFileSize(request.getFile().getSize());
            entity.addFiles(fileEntity);
        }

        boardRepository.save(entity);

        resultMap.put("resultCode", 200);
        resultMap.put("resultMsg", "OK");
        

        return resultMap;
    }

    // 게시글 수정
    @Transactional
    public Map<String, Object> updateBoard(BoardDTO.Request request) throws Exception{
        Map<String,Object> resultMap = new HashMap<>();

        // 물리적으로 저장
        Map<String, Object> fileMap = fileUtils.uploadFiles(request.getFile(), filePath);
        // brdId에 해당하는 게시글 조회, 없으면 예외
        BoardEntity entity = boardRepository.findById(request.getBrdId())
            .orElseThrow(()-> new RuntimeException("게시글이 존재하지 않습니다."));

        // 게시글 정보 수정    
        entity.setTitle(request.getTitle());
        entity.setContents(request.getContents());
        entity.setWriter("admin");

        // 새로운 첨부파일 있으면
        if(fileMap != null){
            // 기존 파일 삭제
            entity.getFileList().forEach(file -> {
            try{
                String fullPath = file.getFilePath() + file.getStoredName();
                fileUtils.deleteFile(fullPath);
            }catch(Exception e){
                throw new RuntimeException("파일 삭제중 오류가 발생하였습니다.");
            }
            });
            // 기존 파일 목록 비우기
            entity.getFileList().clear();

            // 새로운 첨부파일로 생성
            BoardFileEntity fileEntity = new BoardFileEntity();
            fileEntity.setFileName(fileMap.get("fileName").toString());
            fileEntity.setStoredName(fileMap.get("storedFileName").toString());
            fileEntity.setFilePath(fileMap.get("filePath").toString());
            fileEntity.setFileSize(request.getFile().getSize());
            // 새 첨부파일 추가
            entity.addFiles(fileEntity);
        }

        // 수정된 게시글 db에 저장
        boardRepository.save(entity);

        resultMap.put("resultCode", 200);
        resultMap.put("resultMsg", "OK");
        

        return resultMap;
    }

    // 게시글 삭제
    @Transactional
    public Map<String, Object> deleteBoard(@PathVariable("brdId") int brdId) throws Exception{

        Map<String,Object> resultMap = new HashMap<>();
        // brdId에 해당하는 게시글 조회, 없으면 예외
        BoardEntity entity = boardRepository.findById(brdId)
            .orElseThrow(()-> new RuntimeException("게시글이 존재하지 않습니다."));
        // 게시글의 첨부파일 물리적 삭제
        entity.getFileList().forEach(file -> {
            try{
                // 파일 경로
                String fullPath = file.getFilePath() + file.getStoredName();
                fileUtils.deleteFile(fullPath);
            }catch(Exception e){
                throw new RuntimeException("파일 삭제중 오류가 발생하였습니다.");
            }
        });

        // db삭제
        boardRepository.delete(entity);

        resultMap.put("resultCode", 200);
        resultMap.put("resultMsg", "OK");
        
        return resultMap;
    }

    

    // 파일 다운로드
    public ResponseEntity<Resource> downLoadFile(int bfId) throws Exception{
        // http 헤더 객체
        HttpHeaders header = new HttpHeaders();
        Resource resource = null;

        // 파일 정보
        BoardFileDTO fileDTO = 
            BoardFileDTO.of(
                fileRepository
                .findById(bfId)
                .orElseThrow(()-> new NotFoundException("파일 정보 없음"))
            );

        String fullPath = fileDTO.getFilePath() + fileDTO.getStoredName();
        String fileName = fileDTO.getFileName(); // 다운로드 할 때 사용
        
        File f = new File(fullPath);

        if(! f.exists()){
            throw new NotFoundException("파일 정보 없음");
        }

        // 파일타입 > NIO 를 이용한 타입 찾기
        // mimeType >> 미디어 파일인지 이미지 파일인에 따라 파일 다운하는 방식이 다름 
        String mimeType = Files.probeContentType(Paths.get(f.getAbsolutePath()));

        if(mimeType == null){
            mimeType = "application/octet-stream";  // 기본 바이너리 파일
        }
        // 리소스 객체에 url 을 통해서 전송할 파일 저장
        resource = new FileSystemResource(f);
        
        // ContentDisposition : http 응답에서 브라우저가 콘텐츠를 처리하는 방식, 콘텐츠 어떻게 처리할거냐
        // inline > 브라우저 바에서 처리 >> open > pdf같은거 브라우저에서 바로 열 수 있게!
        // attachment > 다운로드
        header.setContentDisposition(
            ContentDisposition
            .builder("attachment")
            .filename(fileName,StandardCharsets.UTF_8)
            .build()
        );

        // mimeType 설정
        header.setContentType(MediaType.parseMediaType(mimeType));
        header.setContentLength(fileDTO.getFileSize());

        // 캐쉬 설정
        header.setCacheControl("no-cache, no-store, must-revalidate");
        header.set("Prama", "no-cache");  // old browser 호환
        header.set("Expires","0");  // 즉시 삭제

        return new ResponseEntity<>(resource, header, HttpStatus.OK);

    }

    // 파일 삭제
    public void delFile(int bfId) throws Exception{

        // 파일 정보
        BoardFileDTO fileDTO = 
            BoardFileDTO.of(
                fileRepository
                .findById(bfId)
                .orElseThrow(()-> new NotFoundException("파일 정보 없음"))
            );

        // 파일 경로
        String fullPath = fileDTO.getFilePath() + fileDTO.getStoredName();
        // 실제 삭제
        fileUtils.deleteFile(fullPath);
        // db 삭제
        fileRepository.deleteById(bfId);   

    }

}
