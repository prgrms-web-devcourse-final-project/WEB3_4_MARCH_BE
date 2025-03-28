package com.backend.domain.image.dto;

public record ImageSaveRequest(
    String image_url,
    boolean is_primary
) {}