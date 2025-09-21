package it.korea.app_boot.gallery.service;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.korea.app_boot.common.dto.PageVO;
import it.korea.app_boot.common.files.FileUtils;
import it.korea.app_boot.gallery.dto.GalleryDTO;
import it.korea.app_boot.gallery.dto.GalleryRequest;
import it.korea.app_boot.gallery.entity.GalleryEntity;
import it.korea.app_boot.gallery.repository.GalleryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GallereyService {

    @Value("${server.file.gallery.path}")
    private String filePath;

    private final GalleryRepository galleryRepository;
    private final FileUtils fileUtils;
    private List<String> extentions = 
                Arrays.asList("jpg", "jpeg", "gif", "png", "webp", "bmp");


    public Map<String,Object> getGalleryList(Pageable pageable) throws Exception{

        Map<String,Object> resultMap = new HashMap<>();
        Page<GalleryEntity> list = galleryRepository.findAll(pageable);

        List<GalleryDTO> gallerys = 
                list.getContent().stream().map(GalleryDTO::of).toList();

        PageVO pageVO = new PageVO();
        pageVO.setData(list.getNumber(), (int)list.getTotalElements());        

        resultMap.put("total", list.getTotalElements());
        resultMap.put("page", list.getNumber());
        resultMap.put("content", gallerys);
        resultMap.put("pageHTML", pageVO.pageHTML());

        return resultMap;

    }
    

    @Transactional
    public void addGallery(GalleryRequest request) throws Exception{
        
        // 파일 업로드 처리
        Map<String, Object> fileMap = this.uploadImageFiles(request.getFile());
        
        GalleryEntity entity = new GalleryEntity();
        
        // 갤러리 랜덤 ID 생성
        String newNums = UUID.randomUUID().toString().replaceAll("-", "").substring(0,10);

        entity.setNums(newNums);
        entity.setTitle(request.getTitle());
        entity.setWriter(request.getWriter());

        entity.setFileName(fileMap.get("fileName").toString());
        entity.setStoredName(fileMap.get("storedFileName").toString());
        entity.setFilePath(filePath);
        entity.setFileThumbName(fileMap.get("thumbName").toString());

        galleryRepository.save(entity);

    }

    // 이미지 수정
    // 코드 순서도 중요
    @Transactional
    public void updateGallery(GalleryRequest request) throws Exception{

        // 엔티티 불러오기
        GalleryEntity galleryEntity = 
            galleryRepository.findById(request.getNums())
                .orElseThrow(()-> new RuntimeException("갤러리 없음"));

        // 삭제하기 위해서 기존 정보를 dto 에 넣음
        GalleryDTO galleryDTO = GalleryDTO.of(galleryEntity);        

        galleryEntity.setTitle(request.getTitle());
                
        Map<String, Object> fileMap = null;

        // 변경할 신규 파일 존재
        if(!request.getFile().isEmpty()){
           // 파일 업로드 처리(신규파일 넣기)
            fileMap = this.uploadImageFiles(request.getFile());
            galleryEntity.setFileName(fileMap.get("fileName").toString());
            galleryEntity.setStoredName(fileMap.get("storedFileName").toString());
            galleryEntity.setFilePath(filePath);
            galleryEntity.setFileThumbName(fileMap.get("thumbName").toString());    
        }
        
        // 업데이트(db에 넣기)
        galleryRepository.save(galleryEntity);

        // 신규 파일 정보가 있다면 기존 파일은 삭제
        if(fileMap != null){
            String oldFilePath = galleryDTO.getFilePath() + galleryDTO.getStoredName();
            String thumbFilePath = galleryDTO.getFilePath() + "thumb" + File.separator + galleryDTO.getFileThumbName();   
            fileUtils.deleteFile(oldFilePath);
            fileUtils.deleteFile(thumbFilePath);
        }

    }

    // 이미지 삭제
    @Transactional
    public void delGallery(String targetIds) throws Exception{

        String[] deleteIds = targetIds.split(",");
        List<GalleryEntity> list = 
            galleryRepository.findByNumsIn(deleteIds);
                
        for(GalleryEntity entity : list){
            // db 삭제        
            galleryRepository.delete(entity);

            // 기존 파일
            String oldFilePath = entity.getFilePath() + entity.getStoredName();
            // 썸네일 파일
            String thumbFilePath = entity.getFilePath() + "thumb" + File.separator + entity.getFileThumbName();
            
            // 삭제
            fileUtils.deleteFile(oldFilePath);
            fileUtils.deleteFile(thumbFilePath);
        }              
        
    }
    
    
    // 파일 업로드 별도 처리
    private Map<String, Object> uploadImageFiles(MultipartFile file) throws Exception{
        
        String fileName = file.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        
        if(!extentions.contains(ext)){
            throw new RuntimeException("파일형식이 맞지 않습니다. 이미지만 가능합니다.");
        }
        
        Map<String,Object> fileMap = fileUtils.uploadFiles(file, filePath);
        
        if(fileMap == null){
            throw new RuntimeException("파일 업로드가 실패했습니다.");
        }   

        String thumbFilePath = filePath + "thumb" + File.separator;
        String storedFilePath = filePath + fileMap.get("storedFileName").toString();

        File thumbFile = new File(storedFilePath);

        if(!thumbFile.exists()){
            throw new RuntimeException("업로드 파일이 존재하지 않음");
        }

        String thumbName = fileUtils.thumbNailFile(150, 150, thumbFile, thumbFilePath);

        fileMap.put("thumbName", thumbName);

        return fileMap;

    }


}