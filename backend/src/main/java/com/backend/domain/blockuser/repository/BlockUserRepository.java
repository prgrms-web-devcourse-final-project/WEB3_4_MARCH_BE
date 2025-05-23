package com.backend.domain.blockuser.repository;

import com.backend.domain.blockuser.entity.BlockUser;
import com.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlockUserRepository extends JpaRepository<BlockUser, Long> {

    boolean existsByBlockerAndBlocked(Member blocker, Member blockedUser);

    Optional<BlockUser> findByBlockerAndBlocked(Member blocker, Member blockedUser);

    List<BlockUser> findAllByBlocker(Member blocker);

    @Query("SELECT b.blocked.id FROM BlockUser b WHERE b.blocker = :me")
    List<Long> findBlockedUserIds(@Param("me") Member me);
}
