package API.BusinessLayer.Storage;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

public class AWSCredentialsHandler implements AwsCredentialsProvider {
    private String ACCESS_KEY = "";
    private String SECRET_KEY = "";

    public AWSCredentialsHandler(String accessKey, String secretKey) {
       this.ACCESS_KEY = accessKey;
       this.SECRET_KEY = secretKey;
    }

    @Override
    public AwsCredentials resolveCredentials() {
        return AwsBasicCredentials.create("", "");
    }
}
