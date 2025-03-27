package com.backend.domain.image.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
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

	// @PostMapping("/presign")
	// public PresignedUrlResponse createPresignedUrl(@PathVariable Long userId,
	// 	@RequestBody PresignedUrlRequest request) {
	//
	// }

	// @PostMapping("/presign")
	// public PresignedUrlResponse createPresignedUrl(@RequestBody PresignedUrlRequest request) {
	// 	return presignedService.createPresignedUrl();
	// }

	@PostMapping("/upload")
	public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
		// 1. 파일 확장자 추출 (ex: png, jpeg)
		String extension = file.getOriginalFilename().split("\\.")[1];

		// 2. 프리사인드 URL 생성
		String presignedUrl = presignedService.createPresignedUrl(file.getOriginalFilename(), file.getContentType());

		// 3. 파일 바이트로 변환
		byte[] fileData = file.getBytes();

		// 4. S3에 파일 업로드
		boolean uploadSuccess = presignedService.uploadFileToS3(presignedUrl, fileData);

		if (uploadSuccess) {
			return ResponseEntity.ok("파일 업로드 성공");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
		}
	}
}
