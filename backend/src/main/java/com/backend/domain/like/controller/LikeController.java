package com.backend.domain.like.controller;

import com.backend.domain.like.dto.LikeMemberProfileDto;
import com.backend.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("/ILikeU")
    public ResponseEntity<List<LikeMemberProfileDto>> getLikedProfilesBySender(@RequestParam Long senderId) {
        List<LikeMemberProfileDto> profiles = likeService.getLikedProfilesBySender(senderId);
        return ResponseEntity.ok(profiles);
    }

    // 나를 좋아한 목록 조회
    @GetMapping("/ULikeMe")
    public ResponseEntity<List<LikeMemberProfileDto>> getLikerProfilesByReceiver(@RequestParam Long receiverId) {
        List<LikeMemberProfileDto> profiles = likeService.getLikerProfilesByReceiver(receiverId);
        return ResponseEntity.ok(profiles);
    }
}
