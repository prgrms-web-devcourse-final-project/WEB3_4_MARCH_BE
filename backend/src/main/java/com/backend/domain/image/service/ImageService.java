package com.backend.domain.image.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.image.dto.ImageResponseDto;
import com.backend.domain.image.entity.Image;
import com.backend.domain.image.repository.ImageRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;
    private final S3Client s3Client;
    private final String bucketName = "devcouse4-team06-bucket";
    private final String urlPrefix = "https://devcouse4-team06-bucket.s3.ap-northeast-2.amazonaws.com/";

    private static final int MAX_IMAGES = 5;

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
        Member member = image.getMember();
        boolean isPrimary = image.getIsPrimary();
        deleteImageFromS3(image.getUrl());
        imageRepository.delete(image);

        if (isPrimary) {
            List<Image> remainingImages = imageRepository.findByMember(member);
            if (!remainingImages.isEmpty()) {
                Image newPrimary = remainingImages.stream()
                    .min(Comparator.comparingLong(Image::getId))
                    .orElse(null);
                if (newPrimary != null) {
                    newPrimary.setIsPrimary(true);
                    member.setProfileImage(newPrimary);
                    imageRepository.save(newPrimary);
                    memberRepository.save(member);
                }
            } else {
                member.setProfileImage(null);
                memberRepository.save(member);
            }
        }
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

    @Transactional
    public void addImages(Long memberId, List<String> imageUrls) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 기존 이미지 개수 확인
        List<Image> currentImages = imageRepository.findByMember(member);
        if (currentImages.size() + imageUrls.size() > MAX_IMAGES) {
            throw new IllegalArgumentException("이미지는 최대 5장까지만 등록할 수 있습니다.");
        }

        boolean hasPrimary = imageRepository.findByMemberAndIsPrimaryTrue(member).isPresent();
        // 새 이미지 DB에 저장
        for (String imageUrl : imageUrls) {
            Image image = Image.builder()
                .url(imageUrl)
                .isPrimary(!hasPrimary)  // 첫 업로드 시 자동 대표 지정
                .member(member)
                .build();
            imageRepository.save(image);
            if (!hasPrimary) {
                member.setProfileImage(image);
                memberRepository.save(member);
                hasPrimary = true;
            }
        }
    }
}