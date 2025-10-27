package com.stationCamera;


import com.stationCamera.entities.Role;
import com.stationCamera.entities.User;
import com.stationCamera.repositories.UserRepository;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.stationCamera.requests.PersonCountRequest;
import com.stationCamera.services.ImageProcessingService;
import com.stationCamera.utils.ImageUtils;
import org.bytedeco.javacv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@ComponentScan(basePackages = "com.stationCamera")
public class StationCameraApplication implements CommandLineRunner {

	RestClient restClient=RestClient.builder().build();
	private final UserRepository userRepository;
	private final ImageProcessingService imageProcessingService;
	private volatile boolean running = true;
	    @Value("${aws.accessKeyId}")
	private  String accessKeyId;
	    @Value("${aws.secretAccessKey}")
	private String secretAccessKey;
	    @Value("${aws.bucketName}")
	private  String bucketName;
		KafkaTemplate
	@Autowired
	public StationCameraApplication(UserRepository userRepository,ImageProcessingService imageProcessingService) {
		this.userRepository = userRepository;
		this.imageProcessingService=imageProcessingService;
	}

	public static void main(String[] args) {
		SpringApplication.run(StationCameraApplication.class, args);
	}

	@Override
	public void run(String... args) {
		setupAdminAccount();
		startCameraSnapshotting();

	}

	private void setupAdminAccount() {

		User adminAccount = userRepository.findByRole(Role.ADMIN);
		if (adminAccount == null) {
			User user = new User();
			user.setEmail("admin@gmail.com");
			user.setFirstName("admin");
			user.setLastName("admin");
			user.setRole(Role.ADMIN);
			user.setPassword(new BCryptPasswordEncoder().encode("admin"));
			userRepository.save(user);
		}
	}

	private void startCameraSnapshotting() {
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
				.withRegion("us-east-1")
				.build();

		new Thread(() -> {
			try (OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0)) {
				grabber.start();

				System.out.println("Camera started, taking periodic snapshots...");

				int snapshotInterval = 10;
				int snapshotCount = 1;

				while (running) {
					org.bytedeco.javacv.Frame frame = grabber.grab();
					if (frame != null) {

						Java2DFrameConverter converter = new Java2DFrameConverter();
						java.awt.image.BufferedImage image = converter.convert(frame);

						// Convert BufferedImage to ByteArray
						byte[] imageBytes = ImageUtils.bufferedImageToByteArray(image);


						uploadImageToS3(s3Client, imageBytes, snapshotCount);

						snapshotCount++;


						TimeUnit.SECONDS.sleep(snapshotInterval);
					}
				}

				grabber.stop();
				System.out.println("Camera snapshotting stopped.");
			} catch (Exception e) {
				System.err.println("Error during snapshot capture: " + e.getMessage());
			}
		}).start();
	}

	private void uploadImageToS3(AmazonS3 s3Client, byte[] imageBytes, int snapshotCount) throws IOException {
//		String bucketName;
		String objectKey = "snapshots/snapshot-" + new Date().toString() + ".jpg";


		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType("image/jpeg");
		metadata.setContentLength(imageBytes.length);

		s3Client.putObject(bucketName, objectKey, new ByteArrayInputStream(imageBytes), metadata);
		System.out.println("Uploaded snapshot to S3: " + objectKey);
		Integer result=imageProcessingService.processLatestImage();
//		Integer result=5;
		PersonCountRequest count=new PersonCountRequest();

		count.setCount(result);

		String response=restClient.post()
				.uri("http://localhost:8081/api/v1/auth/add-person-count/9")
				.body(count)
				.retrieve()
				.body(String.class);
		System.out.println(response);


	}
}
