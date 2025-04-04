package com.backend.domain.like.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.like.dto.MemberProfileDto;
import com.backend.domain.like.service.LikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/profile")
    public ResponseEntity<String> likeProfile(@RequestParam Long senderId,
                                              @RequestParam Long receiverId) {
        likeService.likeProfile(senderId, receiverId);
        return ResponseEntity.ok("프로필 좋아요 및 알림 전송 완료");
    }

    // 내가 좋아요한 목록 조회
    @GetMapping("/I-Like-U")
    public ResponseEntity<List<MemberProfileDto>> getLikedProfilesBySender(@RequestParam Long senderId) {
        List<MemberProfileDto> profiles = likeService.getLikedProfilesBySender(senderId);
        return ResponseEntity.ok(profiles);
    }

    // 나를 좋아한 목록 조회
    @GetMapping("/U-Like-Me")
    public ResponseEntity<List<MemberProfileDto>> getLikerProfilesByReceiver(@RequestParam Long receiverId) {
        List<MemberProfileDto> profiles = likeService.getLikerProfilesByReceiver(receiverId);
        return ResponseEntity.ok(profiles);
    }
}
