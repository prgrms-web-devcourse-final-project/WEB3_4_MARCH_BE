package com.backend.domain.image.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.image.dto.ImageRegisterRequest;
import com.backend.domain.image.entity.Image;
import com.backend.domain.image.repository.ImageRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;

    // 프로필 수정 시 대표 이미지 업데이트
    @Transactional
    public void setPrimaryImage(Member member, Long imageId) {
        // 현재 대표 이미지가 있으면 isPrimary를 false로 업데이트
        Optional<Image> existingPrimary = imageRepository.findByMemberAndIsPrimaryTrue(member);
        existingPrimary.ifPresent(image -> {
            image.updateIsPrimary(false);
            imageRepository.save(image);
        });

        // 선택한 이미지의 isPrimary를 true로 업데이트
        Optional<Image> selectedImage = imageRepository.findById(imageId);
        selectedImage.ifPresent(image -> {
            image.updateIsPrimary(true);
            imageRepository.save(image);
        });
    }

    @Transactional
    public void registerImages(Long memberId, List<ImageRegisterRequest> requests) {
        Member member = memberRepository.findByIdAndIsDeletedFalse(memberId)
            .orElseThrow(() -> new RuntimeException("Member not found"));

        // 대표 이미지(isPrimary)가 정확히 1개 지정되었는지 검증
        long primaryCount = requests.stream()
            .filter(ImageRegisterRequest::isPrimary)
            .count();
        if (primaryCount != 1) {
            throw new IllegalArgumentException("대표 이미지(isPrimary)는 반드시 1개만 지정되어야 합니다. 현재 지정된 개수: " + primaryCount);
        }

        // 기존 이미지 제거
        imageRepository.deleteAll(imageRepository.findByMember(member));

        // 새 이미지 리스트 생성
        List<Image> images = requests.stream()
            .map(req -> Image.builder()
                .url(req.url())
                .isPrimary(req.isPrimary())
                .member(member)
                .build())
            .toList();

        imageRepository.saveAll(images);

        Image primaryImage = images.stream()
            .filter(Image::getIsPrimary)
            .findFirst()
            .orElse(null);
        if (primaryImage != null) {
            member.setProfileImage(primaryImage);
        }
    }
}