package com.backend.domain.image.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.domain.image.repository.ImageRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class PresignedService {

	private final S3Client s3Client;
	private final ImageRepository imageRepository;

	// 파일 업로드 메인 로직
	public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
		List<String> uploadResults = new ArrayList<>();

		for (MultipartFile file : files) {
			try {
				String extension = getFileExtension(file);
				String contentType = determineContentType(file);
				if (!isImageType(contentType)) {
					uploadResults.add(file.getOriginalFilename() + " 업로드 실패: 지원되지 않는 파일 유형");
					continue;
				}

				String presignedUrl = createPresignedUrl(file.getOriginalFilename(), contentType);
				byte[] fileData = file.getBytes();

				if (uploadFileToS3(presignedUrl, fileData, contentType)) {
					uploadResults.add(file.getOriginalFilename() + " 업로드 성공");
				} else {
					uploadResults.add(file.getOriginalFilename() + " 업로드 실패");
				}
			} catch (IOException e) {
				uploadResults.add(file.getOriginalFilename() + " 업로드 중 오류 발생");
			}
		}

		return uploadResults;
	}

	// 파일 확장자 추출
	private String getFileExtension(MultipartFile file) {
		return file.getOriginalFilename().split("\\.")[1];
	}

	// 이미지 파일인지 확인
	private boolean isImageType(String contentType) {
		return contentType != null && contentType.startsWith("image/");
	}

	// 파일의 MIME 타입을 자동으로 결정
	private String determineContentType(MultipartFile file) {
		String contentType = file.getContentType();
		if (contentType == null) {
			contentType = guessContentTypeByExtension(file);
		}
		return contentType;
	}

	// 파일 확장자에 따라 MIME 타입을 추정
	private String guessContentTypeByExtension(MultipartFile file) {
		String extension = getFileExtension(file).toLowerCase();
		switch (extension) {
			case "jpg":
			case "jpeg":
				return "image/jpeg";
			case "png":
				return "image/png";
			case "gif":
				return "image/gif";
			case "bmp":
				return "image/bmp";
			default:
				return null;  // 이미지가 아니면 null 반환
		}
	}

	// 프리사인드 URL 생성
	public String createPresignedUrl(String fileName, String contentType) {
		String bucketName = "devcouse4-team06-bucket";
		String uuid = UUID.randomUUID().toString();
		String key = "images/" + uuid + "." + getFileExtension(fileName);

		S3Presigner presigner = S3Presigner.create();
		PutObjectRequest putObjectRequest = createPutObjectRequest(bucketName, key, contentType);
		PutObjectPresignRequest presignRequest = createPresignRequest(putObjectRequest);

		return presigner.presignPutObject(presignRequest).url().toString();
	}

	// S3에 파일 업로드
	public boolean uploadFileToS3(String presignedUrl, byte[] fileData, String contentType) {
		try {
			HttpURLConnection connection = createHttpURLConnection(presignedUrl, contentType);
			connection.getOutputStream().write(fileData);
			return connection.getResponseCode() == 200;
		} catch (Exception e) {
			return false;
		}
	}

	// PUT 요청을 위한 HttpURLConnection 설정
	private HttpURLConnection createHttpURLConnection(String presignedUrl, String contentType) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(presignedUrl).openConnection();
		connection.setRequestMethod("PUT");
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", contentType);
		return connection;
	}

	// S3 PutObjectRequest 생성
	private PutObjectRequest createPutObjectRequest(String bucketName, String key, String contentType) {
		return PutObjectRequest.builder()
			.bucket(bucketName)
			.key(key)
			.contentType(contentType)
			.build();
	}

	// PresignRequest 생성
	private PutObjectPresignRequest createPresignRequest(PutObjectRequest putObjectRequest) {
		return PutObjectPresignRequest.builder()
			.putObjectRequest(putObjectRequest)
			.signatureDuration(Duration.ofMinutes(10))
			.build();
	}

	// 파일 이름에서 확장자 추출
	private String getFileExtension(String fileName) {
		return fileName.split("\\.")[1];
	}
}