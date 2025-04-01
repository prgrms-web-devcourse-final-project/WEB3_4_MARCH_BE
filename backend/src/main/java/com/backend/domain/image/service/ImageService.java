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
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;

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
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return member.getImages().stream()
            .map(ImageResponseDto::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteImage(Long memberId, Long imageId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Image image = imageRepository.findById(imageId)
            .orElseThrow(() -> new GlobalException(GlobalErrorCode.IMAGE_NOT_FOUND));

        if (!image.getMember().getId().equals(memberId)) {
            throw new GlobalException(GlobalErrorCode.UNAUTHORIZED_IMAGE_OPERATION);
        }

        member = image.getMember();

        // 대표 이미지라면 먼저 profileImage 업데이트
        if (image.getIsPrimary()) {
            // 회원의 모든 이미지 중 현재 삭제 대상 이미지를 제외한 나머지를 가져옴
            List<Image> remainingImages = imageRepository.findByMember(member);
            remainingImages.removeIf(img -> img.getId().equals(imageId));

            if (!remainingImages.isEmpty()) {
                // ID가 가장 낮은 이미지를 새 대표 이미지로 선택
                Image newPrimary = remainingImages.stream()
                    .min(Comparator.comparingLong(Image::getId))
                    .orElse(null);
                if (newPrimary != null) {
                    newPrimary.setIsPrimary(true);
                    member.setProfileImage(newPrimary);
                    imageRepository.save(newPrimary);
                }
            } else {
                // 남은 이미지가 없다면 profileImage를 null로 설정
                member.setProfileImage(null);
            }
            memberRepository.save(member);
        }

        // S3에서 이미지 삭제
        deleteImageFromS3(image.getUrl());
        // DB에서 이미지 삭제
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
    public ImageResponseDto setPrimaryImage(Long memberId, Long imageId) {
        Image image = imageRepository.findById(imageId)
            .orElseThrow(() -> new IllegalArgumentException("Image not found"));

        // 소유자 검증
        if (!image.getMember().getId().equals(memberId)) {
            throw new GlobalException(GlobalErrorCode.UNAUTHORIZED_IMAGE_OPERATION);
        }

        // 요청한 이미지가 이미 대표 이미지인 경우
        if (image.getIsPrimary()) {
            throw new GlobalException(GlobalErrorCode.ALREADY_PRIMARY_IMAGE);
        }

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