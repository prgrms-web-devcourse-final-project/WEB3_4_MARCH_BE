package com.backend.domain.chatrequest.entity;

public enum ChatRequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED;

    public boolean isAccepted() {
        return this == ACCEPTED;
    }

    public boolean isRejected() {
        return this == REJECTED;
    }
}
