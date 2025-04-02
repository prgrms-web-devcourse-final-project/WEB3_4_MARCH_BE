package com.backend.domain.blockuser.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlockUserRequest {

    @JsonProperty("blocker_id")
    private Long blockerId;

    @JsonProperty("blocked_id")
    private Long blockedId;
}
