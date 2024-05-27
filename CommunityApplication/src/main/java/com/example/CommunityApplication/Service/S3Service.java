package com.example.CommunityApplication.Service;

import com.example.CommunityApplication.Config.S3Config;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

@Service
public class S3Service {
    private final S3Client s3Client;
    private final String bucketName;

    public S3Service(S3Config s3Config, S3Client s3Client){
        this.s3Client = s3Client;
        this.bucketName = s3Config.getBucketName();
    }

    public String upload(MultipartFile file) throws IOException {
        String key = String.valueOf(Paths.get(System.currentTimeMillis() + "-" + file.getOriginalFilename()));
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        URL fileUri = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(key));
        return fileUri.toString();
    }
}
