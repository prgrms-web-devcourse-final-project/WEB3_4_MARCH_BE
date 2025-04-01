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
import com.backend.domain.image.repository.ImageRepository;
import com.backend.domain.image.service.ImageService;
import com.backend.domain.image.service.PresignedService;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.response.GenericResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}/images")
public class ImageController {

    private final ImageService imageService;
    private final PresignedService presignedService;
    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;

    private static final int MAX_IMAGES = 5;

    /**
     * 회원의 이미지들을 추가한다.
     * 이미지 파일은 1장 이상 5장 이하여야 하며, S3 업로드 후 DB에 등록된다.
     *
     * @param memberId 회원 ID
     * @param files 업로드할 이미지 파일 배열
     * @return "이미지 등록 완료" 메시지가 포함된 응답 객체
     * @throws IOException 파일 처리 중 예외 발생 시
     */
    @PostMapping
    public ResponseEntity<GenericResponse<String>> addImages(
        @PathVariable Long memberId,
        @RequestPart("files") MultipartFile[] files) throws IOException {

        presignedService.uploadFiles(java.util.Arrays.asList(files), memberId);
        return ResponseEntity.ok(GenericResponse.of("이미지 등록 완료"));
    }

    /**
     * 특정 회원의 이미지 목록을 조회한다.
     *
     * @param memberId 회원 ID
     * @return 해당 회원의 이미지 목록을 포함한 응답 객체
     */
    @GetMapping
    public ResponseEntity<GenericResponse<List<ImageResponseDto>>> getImagesByMember(@PathVariable Long memberId) {
        List<ImageResponseDto> images = imageService.getImagesForMember(memberId);
        return ResponseEntity.ok(GenericResponse.of(images));
    }

    /**
     * 회원의 특정 이미지를 삭제한다.
     * 요청 경로의 회원 ID와 이미지 소유자가 일치해야 하며,
     * 대표 이미지인 경우, 삭제 후 남은 이미지 중 ID가 가장 낮은 이미지가 자동으로 대표 이미지로 설정된다.
     *
     * @param memberId 회원 ID (요청자)
     * @param imageId 삭제할 이미지 ID
     * @return 삭제 성공 메시지를 포함한 응답 객체
     */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<GenericResponse<String>> deleteImage(
        @PathVariable Long memberId,
        @PathVariable Long imageId) {
        imageService.deleteImage(memberId, imageId);
        return ResponseEntity.ok(GenericResponse.of("Image deleted successfully"));
    }

    /**
     * 특정 이미지를 대표 이미지로 설정한다.
     * 요청 경로의 회원 ID와 이미지 소유자가 일치해야 하며,
     * 만약 요청한 이미지가 이미 대표 이미지라면 예외가 발생한다.
     *
     * @param memberId 회원 ID (요청자)
     * @param imageId 대표 이미지로 설정할 이미지 ID
     * @return 업데이트된 이미지 정보를 포함한 응답 객체
     */
    @PatchMapping("/{imageId}/primary")
    public ResponseEntity<GenericResponse<ImageResponseDto>> setPrimaryImage(
        @PathVariable Long memberId,
        @PathVariable Long imageId) {
        ImageResponseDto updatedImage = imageService.setPrimaryImage(memberId, imageId);
        return ResponseEntity.ok(GenericResponse.of(updatedImage));
    }
}
