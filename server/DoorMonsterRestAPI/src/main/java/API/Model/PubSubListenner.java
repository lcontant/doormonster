package API.Model;

import API.Util.Repositories.VideoPubSubRepository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PubSubListenner {
    public String endpoint;
    public String key;
    public String challengKey;
    public int Id;
    public String challengeEndpoint;

    public PubSubListenner() {

    }

    public PubSubListenner(String endpoint, String key, String challengeKey, String challengeEndpoint, int Id) {
        this.endpoint = endpoint;
        this.key = key;
        this.challengeEndpoint =challengeEndpoint;
        this.challengKey = challengeKey;
        this.Id = Id;
    }

    public PubSubListenner(ResultSet resultSet) throws SQLException {
        this(resultSet.getString(VideoPubSubRepository.URL_COLUMN_NAME)
                , resultSet.getString(VideoPubSubRepository.KEY_COLUMN_NAME)
                , resultSet.getString(VideoPubSubRepository.CHALLENGE_KEY)
                , resultSet.getString(VideoPubSubRepository.CHALLENGE_ENDPOINT)
                , resultSet.getInt(VideoPubSubRepository.ID_COLUMN_NAME));
    }


}
