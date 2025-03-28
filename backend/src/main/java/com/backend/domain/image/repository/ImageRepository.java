package com.backend.domain.image.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.domain.image.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

	// 사용자 ID에 해당하는 이미지 리스트 조회
	List<Image> findByUserId(Long userId);

	// 사용자 ID와 대표 이미지 여부를 기준으로 대표 이미지 조회
	Optional<Image> findByUserIdAndIsPrimaryTrue(Long userId);
}
