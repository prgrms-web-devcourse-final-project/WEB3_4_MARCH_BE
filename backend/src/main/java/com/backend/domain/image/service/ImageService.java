package com.backend.domain.image.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.image.dto.ImageRegisterRequest;
import com.backend.domain.image.dto.ImageResponseDto;
import com.backend.domain.image.dto.ImageUpdateRequestDto;
import com.backend.domain.image.entity.Image;
import com.backend.domain.image.repository.ImageRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;
    private final S3Client s3Client;
    private final String bucketName = "devcouse4-team06-bucket";
    private final String urlPrefix = "https://devcouse4-team06-bucket.s3.ap-northeast-2.amazonaws.com/";

    @Transactional
    public void registerImages(Long memberId, List<ImageRegisterRequest> requests) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 이미지를 저장하는 로직 추가
        for (ImageRegisterRequest request : requests) {
            Image image = Image.builder()
                .url(request.url())
                .isPrimary(request.isPrimary())
                .member(member)
                .build();
            imageRepository.save(image);
        }
    }

    @Transactional
    public ImageResponseDto updateImage(Long imageId, ImageUpdateRequestDto requestDto) {
        Image image = imageRepository.findById(imageId)
            .orElseThrow(() -> new IllegalArgumentException("Image not found"));
        image.setIsPrimary(requestDto.getIsPrimary());

        // 대표 이미지 변경인 경우, 기존 대표 이미지 해제
        if (Boolean.TRUE.equals(requestDto.getIsPrimary())) {
            Member member = image.getMember();
            imageRepository.findByMemberAndIsPrimaryTrue(member)
                .stream()
                .filter(img -> !img.getId().equals(imageId))
                .forEach(img -> img.setIsPrimary(false));
            member.setProfileImage(image);
        }
        imageRepository.save(image);
        return ImageResponseDto.from(image);
    }

    @Transactional(readOnly = true)
    public List<ImageResponseDto> getImagesForMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        return member.getImages().stream()
            .map(ImageResponseDto::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
            .orElseThrow(() -> new IllegalArgumentException("Image not found"));
        deleteImageFromS3(image.getUrl());
        imageRepository.delete(image);
    }

    private void deleteImageFromS3(String imageUrl) {
        String key = imageUrl.replace(urlPrefix, "");
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        s3Client.deleteObject(deleteRequest);
    }

    @Transactional
    public ImageResponseDto setPrimaryImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
            .orElseThrow(() -> new IllegalArgumentException("Image not found"));
        Member member = image.getMember();
        // 기존 대표 이미지 해제 (자신 제외)
        imageRepository.findByMemberAndIsPrimaryTrue(member)
            .stream()
            .filter(img -> !img.getId().equals(imageId))
            .forEach(img -> img.setIsPrimary(false));
        // 지정 이미지 대표로 설정
        image.setIsPrimary(true);
        member.setProfileImage(image);
        imageRepository.save(image);
        return ImageResponseDto.from(image);
    }
}