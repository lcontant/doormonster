package API.Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Podcast {
    public String podcastID;
    public String podcastTitle;
    public String podcastSummary;
    public String thumbnailPath;
    public boolean supporterOnly;
    int episodeCount;

    public Podcast() {
    }

    public Podcast(String podcastID, String podcastTitle, String podcastSummary, int episodeCount, String thumbnailPath, boolean supporterOnly) {
        this.podcastID = podcastID;
        this.podcastTitle = podcastTitle;
        this.podcastSummary = podcastSummary;
        this.episodeCount = episodeCount;
        this.thumbnailPath = thumbnailPath;
        this.supporterOnly = supporterOnly;
    }

    public Podcast(ResultSet rs) throws SQLException {
        this(rs.getString("podcastID")
                , rs.getString("podcastTitle")
                , rs.getString("podcastSummary")
                , Integer.parseInt(rs.getString("episodeCount"))
                , rs.getString("thumbnailPath")
                , rs.getBoolean("supporterOnly")
        );

    }
}
