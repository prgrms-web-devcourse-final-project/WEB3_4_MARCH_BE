package com.backend.domain.image.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PresignedUrlRequest {
	private String fileName;
	private String contentType;
}