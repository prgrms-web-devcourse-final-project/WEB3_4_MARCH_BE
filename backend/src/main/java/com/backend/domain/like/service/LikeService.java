package com.backend.domain.like.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.like.dto.MemberProfileDto;
import com.backend.domain.like.entity.Like;
import com.backend.domain.like.repository.LikeRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.notification.entity.NotificationType;
import com.backend.domain.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final NotificationService notificationService;
    private final MemberRepository memberRepository;

    @Transactional
    public void likeProfile(Long senderId, Long receiverId) {
        // 좋아요 정보 저장
        Like like = Like.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .build();
        likeRepository.save(like);

        notificationService.sendNotification(receiverId, NotificationType.LIKE, senderId);
    }
    public List<Like> getLikesGiven(Long senderId) {
        return likeRepository.findBySenderId(senderId);
    }

    public List<Like> getLikesReceived(Long receiverId) {
        return likeRepository.findByReceiverId(receiverId);
    }
    // 내가 좋아요한 목록에서 Receiver 프로필 조회 (내가 좋아한 사람들)
    public List<MemberProfileDto> getLikedProfilesBySender(Long senderId) {
        List<Like> likes = likeRepository.findBySenderId(senderId);
        return likes.stream()
            .map(like -> {
                Member member = memberRepository.findById(like.getReceiverId())
                    .orElse(null);
                if(member != null) {
                    return new MemberProfileDto(member.getId(), member.getNickname(), member.getProfileImage());
                }
                return null;
            })
            .filter(profile -> profile != null)
            .collect(Collectors.toList());
    }

    // 나를 좋아한 목록에서 Sender 프로필 조회 (나를 좋아한 사람들)
    public List<MemberProfileDto> getLikerProfilesByReceiver(Long receiverId) {
        List<Like> likes = likeRepository.findByReceiverId(receiverId);
        return likes.stream()
            .map(like -> {
                Member member = memberRepository.findById(like.getSenderId())
                    .orElse(null);
                if(member != null) {
                    return new MemberProfileDto(member.getId(), member.getNickname(), member.getProfileImage());
                }
                return null;
            })
            .filter(profile -> profile != null)
            .collect(Collectors.toList());
    }
}
