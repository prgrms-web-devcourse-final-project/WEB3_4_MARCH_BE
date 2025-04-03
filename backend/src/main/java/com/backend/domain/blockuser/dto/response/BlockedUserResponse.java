package com.backend.domain.blockuser.dto.response;

import com.backend.domain.blockuser.entity.BlockUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BlockedUserResponse {

    private Long userId;

    private String nickname;

    private Integer age;

    private String gender;

    private LocalDateTime blockedAt;

    public static BlockedUserResponse of(BlockUser blockUser) {
        return BlockedUserResponse.builder()
                .userId(blockUser.getBlocked().getId())
                .age(blockUser.getBlocked().getAge())
                .nickname(blockUser.getBlocked().getNickname())
                .blockedAt(blockUser.getBlockedAt())
                .build();
    }

}
