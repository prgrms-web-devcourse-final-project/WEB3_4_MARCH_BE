package com.backend.domain.like.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.domain.like.entity.Likes;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {
	// 내가 좋아요한 목록
	List<Likes> findBySenderId(Long senderId);

	// 나를 좋아한 목록
	List<Likes> findByReceiverId(Long receiverId);
}
