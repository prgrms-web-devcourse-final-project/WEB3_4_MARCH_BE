package com.backend.domain.image.service;

import java.net.URL;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.backend.domain.image.dto.PresignedUrlRequest;
import com.backend.domain.image.dto.PresignedUrlResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	public PresignedUrlResponse generatePresignedUrl(PresignedUrlRequest request) {
		String fileName = request.getFileName();
		String contentType = request.getContentType();

		// 만료 시간 5분 설정
		Date expiration = new Date();
		expiration.setTime(expiration.getTime() + 1000 * 60 * 5);

		// 요청 생성
		GeneratePresignedUrlRequest generatePresignedUrlRequest =
			new GeneratePresignedUrlRequest(bucketName, fileName)
				.withMethod(HttpMethod.PUT)
				.withExpiration(expiration);
		generatePresignedUrlRequest.setContentType(contentType);

		// S3에서 URL 생성
		URL presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

		// CDN URL (업로드 후 접근용)
		String imageUrl = "https://" + bucketName + ".s3.amazonaws.com/" + fileName;

		return new PresignedUrlResponse(presignedUrl.toString(), imageUrl);
	}
}