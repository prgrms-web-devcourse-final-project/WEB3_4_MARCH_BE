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
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;

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

	private static final int MAX_IMAGES = 5;

	/**
	 * 전달받은 MultipartFile 리스트를 S3에 업로드하고, 업로드 성공한 이미지 URL들을 반환한다.
	 * 또한, 업로드한 이미지를 DB에 등록하고, 첫 업로드 시 자동으로 대표 이미지로 지정한다.
	 *
	 * @param files 업로드할 MultipartFile 리스트
	 * @param memberId 업로드할 회원의 ID
	 * @return 업로드된 이미지 URL 리스트
	 * @throws IOException 파일 바이트 변환 중 예외 발생 시
	 */
	public List<String> uploadFiles(List<MultipartFile> files, Long memberId) throws IOException {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

		// 기존 이미지 개수 확인
		List<Image> currentImages = imageRepository.findByMember(member);
		if (currentImages.size() + files.size() > MAX_IMAGES) {
			throw new GlobalException(GlobalErrorCode.IMAGE_COUNT_INVALID);
		}

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

	/**
	 * S3 업로드를 위한 Presigned URL을 생성한다.
	 *
	 * @param key S3 저장용 키
	 * @param contentType 파일의 MIME 타입
	 * @return 생성된 Presigned URL 문자열
	 */
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

	/**
	 * Presigned URL을 이용하여 S3에 파일 데이터를 업로드한다.
	 *
	 * @param presignedUrl S3에 업로드할 Presigned URL
	 * @param fileData 업로드할 파일 데이터 바이트 배열
	 * @param contentType 파일의 MIME 타입
	 * @return 업로드 성공 여부 (200 응답일 경우 true)
	 */
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