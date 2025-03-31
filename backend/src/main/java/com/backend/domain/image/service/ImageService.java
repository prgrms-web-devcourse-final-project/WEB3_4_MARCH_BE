package com.backend.domain.image.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.image.dto.ImageRegisterRequest;
import com.backend.domain.image.entity.Image;
import com.backend.domain.image.repository.ImageRepository;
import com.backend.domain.member.dto.MemberResponseDto;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponseDto changeRepresentativeImage(Long memberId, Long newPrimaryImageId) {
        // 회원 검증
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 새 대표 이미지가 해당 회원의 이미지인지 확인
        Image newPrimaryImage = imageRepository.findById(newPrimaryImageId)
            .filter(img -> img.getMember().getId().equals(memberId))
            .orElseThrow(() -> new IllegalArgumentException("Image not found for member"));

        // 기존 대표 이미지가 있다면 업데이트
        Optional<Image> existingPrimary = imageRepository.findByMemberAndIsPrimaryTrue(member);
        existingPrimary.ifPresent(image -> {
            image.updateIsPrimary(false);
            imageRepository.save(image);
        });

        // 새 이미지를 대표로 지정
        newPrimaryImage.updateIsPrimary(true);
        imageRepository.save(newPrimaryImage);

        // 회원 엔티티의 profileImage 업데이트
        member.setProfileImage(newPrimaryImage);

        return MemberResponseDto.from(member);
    }

    @Transactional(readOnly = true)
    public List<Image> getImagesForMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        return imageRepository.findByMember(member);
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