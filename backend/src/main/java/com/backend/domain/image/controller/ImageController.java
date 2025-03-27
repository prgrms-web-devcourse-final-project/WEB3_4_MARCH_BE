package com.backend.domain.image.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.image.dto.PresignedUrlRequest;
import com.backend.domain.image.dto.PresignedUrlResponse;
import com.backend.domain.image.service.ImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}/images")
public class ImageController {

	private final ImageService imageService;

	@PostMapping("/presign")
	public PresignedUrlResponse generatePresignedUrl(
		@PathVariable Long userId,
		@RequestBody PresignedUrlRequest request
	) {
		return imageService.generatePresignedUrl(request);
	}
}
