package com.example.ask_hub.global.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file) {

        validateFile(file);

        String fileName = createFileName(file.getOriginalFilename());

        try {

            PutObjectRequest request =
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize()
                    )
            );

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패");
        }

        return getFileUrl(fileName);
    }

    public void delete(String fileUrl) {

        String key = extractKeyFromUrl(fileUrl);

        DeleteObjectRequest request =
                DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build();

        s3Client.deleteObject(request);
    }

    private void validateFile(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일입니다.");
        }
    }

    private String createFileName(String originalFilename) {

        return UUID.randomUUID() + "_" + originalFilename;
    }

    private String getFileUrl(String fileName) {

        return String.format(
                "https://%s.s3.ap-northeast-2.amazonaws.com/%s",
                bucket,
                fileName
        );
    }

    private String extractKeyFromUrl(String fileUrl) {

        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    public String convertToPresignedUrl(String objectUrl) {
        String key = extractKeyFromUrl(objectUrl);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}