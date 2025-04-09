package com.backend.domain.image.service;

import com.backend.domain.image.dto.ImageResponseDto;
import com.backend.domain.image.entity.Image;
import com.backend.domain.image.repository.ImageRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 회원의 이미지 목록을 조회한다.
     *
     * @param memberId 조회할 회원의 ID
     * @return 회원의 이미지 목록을 DTO 형태로 변환한 리스트
     */
    @Transactional(readOnly = true)
    public List<ImageResponseDto> getImagesForMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND));

        return member.getImages().stream()
            .map(ImageResponseDto::from)
            .collect(Collectors.toList());
    }

    /**
     * 회원의 특정 이미지를 삭제한다.
     * 삭제 전, 회원 ID와 이미지 소유자가 일치하는지 검증하며,
     * 대표 이미지인 경우, 남은 이미지 중 ID가 가장 낮은 이미지로 새 대표 이미지가 설정된다.
     *
     * @param memberId 요청한 회원 ID
     * @param imageId 삭제할 이미지 ID
     */
    @Transactional
    public void deleteImage(Long memberId, Long imageId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND));

        Image image = imageRepository.findById(imageId)
            .orElseThrow(() -> new GlobalException(GlobalErrorCode.IMAGE_NOT_FOUND));

        if (!image.getMember().getId().equals(memberId)) {
            throw new GlobalException(GlobalErrorCode.UNAUTHORIZED_IMAGE_OPERATION);
        }

        member = image.getMember();

        List<Image> currentImages = imageRepository.findByMember(member);
        if (currentImages.size() <= 1) {
            throw new GlobalException(GlobalErrorCode.IMAGE_COUNT_INVALID, "최소 1장의 이미지는 유지되어야 합니다.");
        }

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

        // 부모 컬렉션에서 제거 (orphanRemoval=true)
        member.getImages().remove(image);
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

    /**
     * 회원의 특정 이미지를 대표 이미지로 설정한다.
     * 요청자의 회원 ID와 이미지 소유자가 일치해야 하며,
     * 만약 요청한 이미지가 이미 대표 이미지라면 예외가 발생한다.
     *
     * @param memberId 요청한 회원 ID
     * @param imageId 대표로 설정할 이미지 ID
     * @return 대표 이미지로 업데이트된 이미지의 DTO
     */
    @Transactional
    public ImageResponseDto setPrimaryImage(Long memberId, Long imageId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new GlobalException(GlobalErrorCode.MEMBER_NOT_FOUND));

        Image image = imageRepository.findById(imageId)
            .orElseThrow(() -> new GlobalException(GlobalErrorCode.IMAGE_NOT_FOUND));

        // 소유자 검증
        if (!image.getMember().getId().equals(memberId)) {
            throw new GlobalException(GlobalErrorCode.UNAUTHORIZED_IMAGE_OPERATION);
        }

        // 요청한 이미지가 이미 대표 이미지인 경우
        if (image.getIsPrimary()) {
            throw new GlobalException(GlobalErrorCode.ALREADY_PRIMARY_IMAGE);
        }

        member = image.getMember();
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