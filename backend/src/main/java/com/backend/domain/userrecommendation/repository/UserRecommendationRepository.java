package com.backend.domain.userrecommendation.repository;

import com.backend.domain.userrecommendation.entity.UserRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface UserRecommendationRepository extends JpaRepository<UserRecommendation, Long> {

    @Query("SELECT ur.recommendedUser.id FROM UserRecommendation ur WHERE ur.receivingUser = :user")
    Set<Long> findAllByReceivingUser(@Param("user") Member user);
}
