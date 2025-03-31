package com.backend.domain.image.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.domain.image.entity.Image;
import com.backend.domain.image.repository.ImageRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;

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
	private final S3Presigner s3Presigner;
	private final ImageRepository imageRepository;
	private final MemberRepository memberRepository;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	@Value("${cloud.aws.s3.url-prefix}")
	private String urlPrefix;

	public List<String> uploadFiles(List<MultipartFile> files, Long memberId) throws IOException {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("Member not found"));

		List<String> uploadResults  = new ArrayList<>();
		boolean hasPrimary = imageRepository.findByMemberAndIsPrimaryTrue(member).isPresent();

		for (MultipartFile file : files) {
			String originalFilename = Optional.ofNullable(file.getOriginalFilename()).orElse("default.jpg");
			String extension = getExtension(file.getOriginalFilename());
			String uuid = UUID.randomUUID().toString();
			String key = "images/" + uuid + "." + extension;
			String imageUrl = urlPrefix + key;

			String contentType = Optional.ofNullable(file.getContentType()).orElse("image/jpeg");
			String presignedUrl = createPresignedUrl(key, contentType);

			boolean uploadSuccess = uploadFileToS3(presignedUrl, file.getBytes(), contentType);
			if (uploadSuccess) {
				Image image = Image.builder()
					.url(imageUrl)
					.isPrimary(!hasPrimary)  // 첫 업로드 시 자동 대표 지정
					.member(member)
					.build();
				imageRepository.save(image);
				uploadResults.add(imageUrl);
				if (!hasPrimary) {
               		member.setProfileImage(image);
                	memberRepository.save(member);
					hasPrimary = true;
				}
			} else {
				uploadResults.add("Failed to upload: " + file.getOriginalFilename());
			}
		}
		return uploadResults;
	}

	public String createPresignedUrl(String key, String contentType) {
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(key)
			.contentType(contentType)
			.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.putObjectRequest(putObjectRequest)
			.signatureDuration(Duration.ofMinutes(10))
			.build();

		PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
		return presignedRequest.url().toString();
	}

	public boolean uploadFileToS3(String presignedUrl, byte[] fileData, String contentType) {
		try {
			URI uri = new URI(presignedUrl);
			HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
			connection.setRequestMethod("PUT");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", contentType);
			connection.getOutputStream().write(fileData);
			return connection.getResponseCode() == 200;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private String getExtension(String fileName) {
		return Optional.ofNullable(fileName)
			.filter(f -> f.contains("."))
			.map(f -> f.substring(fileName.lastIndexOf(".") + 1))
			.orElse("jpg");
	}
}