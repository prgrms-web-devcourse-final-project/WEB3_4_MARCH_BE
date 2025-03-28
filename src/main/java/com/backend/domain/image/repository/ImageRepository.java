package com.backend.domain.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.image.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

    // List<Image> findAllByUser(Member user);
    //
    // Optional<Image> findByUserAndIsPrimaryTrue(Member user);
}
