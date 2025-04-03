package com.backend.domain.blockuser.service;

import com.backend.domain.blockuser.dto.response.BlockedUserResponse;
import com.backend.domain.blockuser.entity.BlockUser;
import com.backend.domain.blockuser.repository.BlockUserRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.auth.model.CustomUserDetails;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockUserService {

    private final BlockUserRepository blockUserRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void blockUser(CustomUserDetails loginUser, Long blockedId) {

        Long blockerId = loginUser.getMemberId();

        Member blocker = blockerIdExists(blockerId);
        Member blockedUser = blockedIdExists(blockedId);

        if (blockerId.equals(blockedId)) {
            throw new GlobalException(GlobalErrorCode.INVALID_BLOCK_SELF);
        }

        if (blockUserRepository.exitsByBlockerAndBlockedUser(blocker, blockedUser)) {
            throw new GlobalException(GlobalErrorCode.USER_ALREADY_BLOCKED);
        }

        BlockUser blockUser = BlockUser.builder()
                .blocker(blocker)
                .blocked(blockedUser)
                .build();

        blockUserRepository.save(blockUser);
    }

    @Transactional
    public void unblockUser(CustomUserDetails loginUser, Long blockedId) {

        Long blockerId = loginUser.getMemberId();

        Member blocker = blockerIdExists(blockerId);
        Member blockedUser = blockedIdExists(blockedId);

        BlockUser blockUserEntity = blockUserRepository.findByBlockerAndBlockedUser(blocker, blockedUser).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.ALREADY_UNBLOCK)
        );

        blockUserRepository.delete(blockUserEntity);
    }

    @Transactional(readOnly = true)
    public List<BlockedUserResponse> getBlockedUsers(CustomUserDetails loginUser) {

        Member blocker = blockerIdExists(loginUser.getMemberId());

        return blockUserRepository.findAllByBlocker(blocker).stream()
                .map(BlockedUserResponse :: of)
                .toList();
    }


    private Member blockerIdExists(Long blockerId) {
        Member blocker = memberRepository.findById(blockerId).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.BLOCKER_NOT_FOUND)
        );

        return blocker;
    }

    private Member blockedIdExists(Long blockedId) {
        Member blockedUser = memberRepository.findById(blockedId).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.BLOCKED_USER_NOT_FOUND)
        );

        return blockedUser;
    }




}
