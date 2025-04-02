package com.backend.domain.blockuser.service;

import com.backend.domain.blockuser.entity.BlockUser;
import com.backend.domain.blockuser.repository.BlockUserRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlockUserService {

    private final BlockUserRepository blockUserRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void blockUser(Long blockerId, Long blockedId) {

        Member blocker = memberRepository.findById(blockerId).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.BLOCKER_NOT_FOUND)
        );
        Member blockedUser = memberRepository.findById(blockedId).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.BLOCKED_USER_NOT_FOUND)
        );

        if (blockerId.equals(blockedId)) {
            throw new GlobalException(GlobalErrorCode.INVALID_BLOCK_SELF);
        }

        if (blockUserRepository.exitsByBlockerAndBlockedUser(blocker, blockedUser)) {
            throw new GlobalException(GlobalErrorCode.USER_ALREADY_BLOCKED);
        }

        BlockUser blockUser = BlockUser.builder()
                .blocker(blocker)
                .blockedUser(blockedUser)
                .build();

        blockUserRepository.save(blockUser);
    }




}
