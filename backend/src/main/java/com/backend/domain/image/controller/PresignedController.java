package com.backend.domain.image.controller;

import java.io.IOException;
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
	public ResponseEntity<String> uploadImages(@RequestParam("files") List<MultipartFile> files) {
		try {
			List<String> uploadResults = presignedService.uploadFiles(files);
			return ResponseEntity.ok("업로드 결과: " + String.join(", ", uploadResults));
		} catch (IOException e) {
			return ResponseEntity.status(500).body("파일 업로드 중 오류가 발생했습니다.");
		}
	}
}