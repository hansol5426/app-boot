package it.korea.app_boot.board.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

// sql에서 가져온 데이터를 담는 클래스
public class Board {

	@Data
	public static class Request{
		private int brdId;
		private String title;
		private String contents;
		private String writer;
		// 첨부파일 > 파일객체 이름이랑 똑같이 주면됨 / 멀티파일 일시 배열로 주면됨(MultipartFile[]) 
		private MultipartFile file;
	}
	
	@Data
	public static class Response{
		private int brdId;
		private String title;
		private String contents;
		private String writer;
		private int readCount;
		private int likeCount;
		private LocalDateTime modifiedDate;
	}
	
	@Data
	public static class Detail{
		private int brdId;
		private String title;
		private String writer;
		private String contents;
		private int readCount;
		private int likeCount;
		private LocalDateTime createDate;
		private LocalDateTime updateDate;
		// 첨부파일 리스트
		List<BoardFiles> files;
		
		// files getter
		// list에 값이 없을 시 null 처리 위해 초기화
		// 초기화되면 사이즈가 0 >> 사이즈로 체크 가능
		public List<BoardFiles> getFiles(){
			if(this.files == null) {
				this.files = new ArrayList<>();
			}
			return this.files;
		}
	}
	
	// 파일 등록
	@Data
	public static class BoardFiles{
		private int bfId;
		private int brdId;
		private String fileName;
		private String storedName;
		private String filePath;
		private long fileSize;
	}
	
}
