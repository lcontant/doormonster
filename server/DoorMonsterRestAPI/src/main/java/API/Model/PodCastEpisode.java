package API.Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class PodCastEpisode {
    public String podcast;
    public int episodeNum;
    public String episodeName;
    public String episodeLink;
    public String episodeThumbnail;
    public String episodeDescription;
    public LocalDateTime episodePublishDate;
    public String episodeLength;
    public int episodeID;
    public String episodeKeywords;
    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public PodCastEpisode(String podcast, int episodeNum, String episodeName, String episodeLink, String episodeThumbnail, LocalDateTime episodePublishDate, String episodeLength, int episodeId, String episodeKeywords, String episodeDescription) {
        this.podcast = podcast;
        this.episodeNum = episodeNum;
        this.episodeName = episodeName;
        this.episodeLink = episodeLink;
        this.episodeThumbnail = episodeThumbnail;
        this.episodePublishDate = episodePublishDate;
        this.episodeLength = episodeLength;
        this.episodeID = episodeId;
        this.episodeKeywords = episodeKeywords;
        this.episodeDescription = episodeDescription;
    }

    public PodCastEpisode(ResultSet rs) throws SQLException  {
            this(rs.getString("Podcast")
                    , Integer.parseInt(rs.getString("episodeNum"))
                    , rs.getString("episodeName")
                    , rs.getString("episodeLink")
                    , rs.getString("episodeThumbnail")
                    , rs.getTimestamp("episodePublishDate").toLocalDateTime()
                    , rs.getString("episodeLength")
                    , Integer.parseInt(rs.getString("episodeID"))
                    , rs.getString("episodeKeyWords")
                    , rs.getString("episodeDescription"));
    }

    public PodCastEpisode() {

    }
}
