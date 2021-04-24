package API.Model;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class Video {
    public int id;
    public String videoID;
    public String videoTitle;
    public String videoCategory;
    public String videoSummary;
    public String videoFileLink;
    public LocalDateTime videoPublishDate;
    public boolean published;

    //TODO : Split keywords into a list
    public String videoKeyWords;
    public String videoThumbnail;
    public int views;
    public int commentCount;
    private static DateFormat format = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");

    public Video() {}

    public Video(int id, String videoID, String videoTitle,  String videoCategory
            , String videoThumbnail, LocalDateTime videoPublishDate, String videoKeyWords, int views
            , int commentCount,String videoSummary, boolean videoPublished, String videoFileLink) {
        this.videoID = videoID;
        this.id= id;
        this.videoTitle = videoTitle;
        this.videoCategory = videoCategory;
        this.videoPublishDate = videoPublishDate;
        this.videoThumbnail = videoThumbnail;
        this.videoKeyWords = videoKeyWords;
        this.views = views;
        this.videoFileLink = videoFileLink;
        this.commentCount = commentCount;
        this.videoSummary = videoSummary;
        this.published = videoPublished;
    }

    public Video(ResultSet rs) throws SQLException {
        this(
                rs.getInt("id"),
                rs.getString("videoId")
                , rs.getString("videoTitle")
                , rs.getString("videoCategory")
                , rs.getString("videoThumbnail")
                , rs.getTimestamp("videoPublishDate").toLocalDateTime()
                , rs.getString("videoKeyWords")
                , rs.getInt("views"),
                0,
                rs.getString("videoSummary"),
                rs.getBoolean("videoPublished"),
                rs.getString("videoFileLink")
        );

    }

    public Video(ResultSet rs, boolean withComment) throws SQLException {
        this(
                rs.getInt("id"),
                rs.getString("mediaId")
                , rs.getString("videoTitle")
                , rs.getString("videoCategory")
                , rs.getString("videoThumbnail")
                , rs.getTimestamp("videoPublishDate").toLocalDateTime()
                , rs.getString("videoKeyWords")
                , rs.getInt("views")
                , rs.getInt("commentCount")
                , rs.getString("videoSummary")
                , rs.getBoolean("videoPublished")
                , rs.getString("videoFileLink")
        );
    }
}
