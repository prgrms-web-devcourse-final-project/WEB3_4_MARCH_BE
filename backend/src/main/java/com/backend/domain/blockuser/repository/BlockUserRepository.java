package com.backend.domain.blockuser.repository;

import com.backend.domain.blockuser.entity.BlockUser;
import com.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockUserRepository extends JpaRepository<BlockUser, Long> {

    boolean exitsByBlockerAndBlockedUser(Member blocker, Member blockedUser);
}
