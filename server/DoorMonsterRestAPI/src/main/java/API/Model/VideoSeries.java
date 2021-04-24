package API.Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VideoSeries {
    public static final String TABLE_NAME = "VideoSeries";
    public static final String ID_COLUMN_NAME = "ID";
    public static final String VIDEO_ID_COLUMN_NAME = "VIDEO_ID";
    public static final String SERIES_ID_COLUMN_NAME = "SERIES_ID";

    public int id;
    public int videoId;
    public int seriesId;

    public VideoSeries(int id, int videoId, int seriesId) {
        this.id = id;
        this.videoId = videoId;
        this.seriesId = seriesId;
    }

    public VideoSeries(int videoId, int seriesId) {
       this.videoId = videoId;
       this.seriesId = seriesId;
    }

    public VideoSeries(ResultSet rs) throws SQLException {
        this(rs.getInt(ID_COLUMN_NAME)
                , rs.getInt(VIDEO_ID_COLUMN_NAME)
                , rs.getInt(SERIES_ID_COLUMN_NAME));
    }

    public VideoSeries() {

    }

    @Override
    public boolean equals(Object obj) {
        VideoSeries otherVideoSeries = (VideoSeries) obj;
        return otherVideoSeries.id == this.id || (otherVideoSeries.seriesId == seriesId && otherVideoSeries.videoId == this.videoId);
    }
}
