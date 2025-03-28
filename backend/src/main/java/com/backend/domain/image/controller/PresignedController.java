package com.backend.domain.image.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.domain.image.service.PresignedService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
// @RequestMapping("/api/users/{userId}/images")
@RequestMapping("/api/images")
public class PresignedController {
	private final PresignedService presignedService;

	@PostMapping("/uploads")
	public ResponseEntity<String> uploadImages(@RequestParam("files") List<MultipartFile> files) throws IOException {
		List<String> uploadResults  = new ArrayList<>();

		for (MultipartFile file : files) {
			try {
				// 1. 파일의 확장자 추출
				String extension = file.getOriginalFilename().split("\\.")[1];

				// 2. 프리사인드 URL 생성
				String presignedUrl = presignedService.createPresignedUrl(file.getOriginalFilename(), file.getContentType());

				// 3. 파일 바이트로 변환
				byte[] fileData = file.getBytes();

				// 4. S3에 파일 업로드
				boolean uploadSuccess = presignedService.uploadFileToS3(presignedUrl, fileData);

				if (uploadSuccess) {
					uploadResults.add(file.getOriginalFilename() + " 업로드 성공");
				} else {
					uploadResults.add(file.getOriginalFilename() + " 업로드 실패");
				}
			} catch (Exception e) {
				e.printStackTrace();
				// 에러가 발생한 경우 해당 파일명을 결과에 추가
				uploadResults.add(file.getOriginalFilename() + " 업로드 중 오류 발생");
				e.printStackTrace();
			}
		}
		return ResponseEntity.ok("업로드 결과: " + String.join(", ", uploadResults));
	}
}
