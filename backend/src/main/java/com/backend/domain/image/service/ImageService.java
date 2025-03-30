package com.backend.domain.image.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.image.entity.Image;
import com.backend.domain.image.repository.ImageRepository;
import com.backend.domain.member.entity.Member;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

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
}
