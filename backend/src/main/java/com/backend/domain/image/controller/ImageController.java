package com.backend.domain.image.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.domain.image.dto.ImageResponseDto;
import com.backend.domain.image.service.ImageService;
import com.backend.domain.image.service.PresignedService;
import com.backend.global.response.GenericResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}/images")
public class ImageController {

    private final ImageService imageService;
    private final PresignedService presignedService;

    @PostMapping
    public ResponseEntity<GenericResponse<String>> registerImages(
        @PathVariable Long memberId,
        @RequestPart("files") MultipartFile[] files) throws IOException {
        presignedService.uploadFiles(java.util.Arrays.asList(files), memberId);
        return ResponseEntity.ok(GenericResponse.of("이미지 등록 완료"));
    }

    // 회원 ID에 해당하는 이미지 전체 조회
    @GetMapping
    public ResponseEntity<GenericResponse<List<ImageResponseDto>>> getImagesByMember(@PathVariable Long memberId) {
        List<ImageResponseDto> images = imageService.getImagesForMember(memberId);
        return ResponseEntity.ok(GenericResponse.of(images));
    }

    // 이미지 삭제
    @DeleteMapping("/{imageId}")
    public ResponseEntity<GenericResponse<String>> deleteImage(@PathVariable Long imageId) {
        imageService.deleteImage(imageId);
        return ResponseEntity.ok(GenericResponse.of("Image deleted successfully"));
    }

    // 대표 이미지 변경
    @PatchMapping("/{imageId}/primary")
    public ResponseEntity<GenericResponse<ImageResponseDto>> setPrimaryImage(@PathVariable Long imageId) {
        ImageResponseDto updatedImage = imageService.setPrimaryImage(imageId);
        return ResponseEntity.ok(GenericResponse.of(updatedImage));
    }
}
