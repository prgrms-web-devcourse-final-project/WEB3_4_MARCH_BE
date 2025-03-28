package com.backend.domain.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.image.entity.ImageEntity;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    // List<Image> findAllByUser(Member user);
    //
    // Optional<Image> findByUserAndIsPrimaryTrue(Member user);
}
