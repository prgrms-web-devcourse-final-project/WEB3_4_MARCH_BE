package com.backend.domain.image.service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.backend.domain.image.repository.ImageRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class PresignedService {

	private final S3Client s3Client;
	private final ImageRepository imageRepository;

	public String createPresignedUrl(String fileName, String contentType) {
		String urlPrefix = "https://devcouse4-team06-bucket.s3.ap-northeast-2.amazonaws.com/";
		String bucketName = "devcouse4-team06-bucket";
		String uuid = UUID.randomUUID().toString();
		String key = "images/" + uuid + "." + fileName.split("\\.")[1];
		//https://devcouse4-team06-bucket.s3.ap-northeast-2.amazonaws.com/UUID.png
		//db 저장

		// S3Presigner 생성
		S3Presigner presigner = S3Presigner.create();
		// PutObjectRequest 생성
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(key)
			.contentType(contentType)
			.build();

		// Presign 요청 생성
		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.putObjectRequest(putObjectRequest)
			.signatureDuration(Duration.ofMinutes(10))
			.build();

		// Presigned URL 생성
		PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);


		return presignedRequest.url().toString();
	}

	public boolean uploadFileToS3(String presignedUrl, byte[] fileData) {
		try	{
			// HTTP PUT 요청을 통해 S3에 파일 업로드
			HttpURLConnection connection = (HttpURLConnection) new URL(presignedUrl).openConnection();
			connection.setRequestMethod("PUT");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "image/jpeg");  // MIME 타입 설정
			connection.getOutputStream().write(fileData);  // 파일 데이터 전송

			// 응답 코드 확인 (200이면 성공)
			int responseCode = connection.getResponseCode();
			return responseCode == 200;
		} catch (Exception e) {
			e.printStackTrace();
			return false;  // 실패 시 false 반환
		}
	}
}