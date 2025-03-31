package com.backend.domain.image.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.image.dto.ImageRegisterRequest;
import com.backend.domain.image.service.ImageService;
import com.backend.global.response.GenericResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<GenericResponse<String>> registerImages(
            @PathVariable Long memberId,
            @RequestBody List<ImageRegisterRequest> requests) {

        imageService.registerImages(memberId, requests);
        return ResponseEntity.ok(GenericResponse.of("이미지 등록 완료"));
    }
}
