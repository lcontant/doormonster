package API.BusinessLayer.Storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.HashMap;

@Component
public class StorageHandler {

    private S3Client s3;
    String bucket = "doormonster";
    public static String BASE_AWS_URL = "BASE_AWS_URL";


    private static String[] ACCEPTED_USER_IMAGE_FORMATS = {
            ".jpg",
            ".jpeg",
            ".gif",
            ".png",
            ".ico",
            ".bmp"
    };


    public StorageHandler(@Value("${aws.accessKey}") String accessKey, @Value("${aws.accessKey}") String secretKey) throws IOException {
        Region region = Region.US_EAST_1;
        AwsCredentialsProvider credentialsProvider = new AWSCredentialsHandler(accessKey, secretKey);
        AwsCredentials credentials = credentialsProvider.resolveCredentials();
        s3 = S3Client.builder().serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build()).credentialsProvider(StaticCredentialsProvider.create(credentials)).region(region).build();
    }

    public String storeUserProfileImage(MultipartFile file, String name, String originalFileName) throws IOException {

        String[] sections = originalFileName.split("\\.");
        String extension = "." + sections[sections.length - 1];
        PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucket).key("assets/images/user/" + name + extension).build();
        PutObjectResponse response = s3.putObject(objectRequest, RequestBody.fromBytes(file.getBytes()));
        return file.getOriginalFilename();
    }

    public void storeRssFeed(String feedContent) {
        HashMap<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", "application/rss+xml");
        PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucket).key("assets/rss/feed.xml").build();

            s3.putObject(objectRequest, RequestBody.fromBytes(feedContent.getBytes()));
    }

    public String storeVideoThumbnail(MultipartFile file, String series) throws IOException {
        PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucket).key("assets/images/videos/" + file.getOriginalFilename()).build();
        s3.putObject(objectRequest, RequestBody.fromBytes(file.getBytes()));
        return file.getOriginalFilename();
    }

    public String getFileExtension(String fileName) {
        String[] sections = fileName.split("\\.");
        return "." + sections[sections.length - 1];
    }

    public boolean fileIsSupportedFormat(String filename) {
        boolean isAcceptedFormat = false;
        String currentFileExtension = this.getFileExtension(filename);
        currentFileExtension = currentFileExtension.toLowerCase();

        for (String extension : ACCEPTED_USER_IMAGE_FORMATS) {
            if (extension.equals(currentFileExtension)) {
                isAcceptedFormat = true;
                break;
            }
        }
        return isAcceptedFormat;
    }

    public String storePodcastAudioFile(MultipartFile file) throws IOException, AwsServiceException  {
        PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucket).key("assets/podcasts/" + file.getOriginalFilename()).build();
        PutObjectResponse response = s3.putObject(objectRequest, RequestBody.fromBytes(file.getBytes()));
        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new IOException("There was a problem uploading the audio");
        }
        return objectRequest.key();
    }

    public String storePodcastImageFile(MultipartFile file) throws IOException {
        PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucket).key("assets/images/podcasts/" + file.getOriginalFilename()).build();
        PutObjectResponse response = s3.putObject(objectRequest, RequestBody.fromBytes(file.getBytes()));
        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new IOException("There was a problem uploading the audio");
        }
        return objectRequest.key();
    }

    public String storePodcastSeriesThumbnail(MultipartFile file) throws IOException {
        PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucket).key("assets/images/" + file.getOriginalFilename()).build();
        PutObjectResponse response = s3.putObject(objectRequest, RequestBody.fromBytes(file.getBytes()));
        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new IOException("There was a problem uploading the audio");
        }
        return objectRequest.key();
    }

    public String storeVideoFile(MultipartFile file) throws IOException {
       PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucket).key("assets/videos/" + file.getOriginalFilename()).build();
       PutObjectResponse response = s3.putObject(objectRequest, RequestBody.fromBytes(file.getBytes()));
       if (!response.sdkHttpResponse().isSuccessful()) {
          throw new IOException("There was a problem uploading the video file");
       }
       return objectRequest.key();
    }

    public String storeSeriesThumbnailFile(MultipartFile file, String seriesTextId) throws IOException {
        PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucket).key("assets/images/videos/banner_" + seriesTextId + ".jpg").build();
        PutObjectResponse response = s3.putObject(objectRequest, RequestBody.fromBytes(file.getBytes()));
        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new IOException("There was a problem uploading the video file");
        }
        return objectRequest.key();
    }


}
