package com.stationCamera.services.impl;


import com.stationCamera.services.ImageProcessingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.Comparator;
import java.util.Optional;

@Service
public class ImageProcessingServiceImpl implements ImageProcessingService {
    @Value("${aws.accessKeyId}")
    private  String awsAccessKey;
    @Value("${aws.secretAccessKey}")
    private  String awsSecretAccessKey;
    @Value("${aws.bucketName}")
    private  String awsBucketName;
    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(awsAccessKey, awsSecretAccessKey);

    private final String bucketName = awsBucketName;
    private final RekognitionClient rekognitionClient = RekognitionClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
            .build();

    private final S3Client s3Client = S3Client.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
            .build();


    //    @Scheduled(fixedRate = 60)
    @Override
    public Integer processLatestImage() {
        System.out.println("Checking for the latest image...");

        try {

            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();
            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

            Optional<S3Object> latestImage = listResponse.contents().stream()
                    .max(Comparator.comparing(S3Object::lastModified));

            if (latestImage.isPresent()) {
                String key = latestImage.get().key();
                System.out.println("Processing latest image: " + key);

                DetectLabelsRequest request = DetectLabelsRequest.builder()
                        .image(Image.builder()
                                .s3Object(software.amazon.awssdk.services.rekognition.model.S3Object.builder()
                                        .bucket(bucketName)
                                        .name(key)
                                        .build())
                                .build())
                        .maxLabels(10)
                        .minConfidence(75F)
                        .build();

                DetectLabelsResponse response = rekognitionClient.detectLabels(request);

                int personCount = response.labels().stream()
                        .filter(label -> label.name().equalsIgnoreCase("Person"))
                        .mapToInt(label -> label.instances().size())
                        .sum();

                System.out.println("Latest Image: " + key + " - People Count: " + personCount);
                return personCount;
            } else {
                System.out.println("No images found in the bucket.");
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}

