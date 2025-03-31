package com.backend.domain.image.dto;

public record ImageRegisterRequest(
    String url,
    boolean isPrimary
) {}
