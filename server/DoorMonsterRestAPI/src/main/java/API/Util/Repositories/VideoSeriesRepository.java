package API.Util.Repositories;

import API.Model.Series;
import API.Model.Video;
import API.Model.VideoSeries;
import API.Ressource.SeriesWithVideos;
import API.Util.SQLConnector.ConnectionPoolManager;
import java.util.HashMap;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class VideoSeriesRepository {

  private static final String ADD_VIDEO_TO_SERIES_REQUEST = String.format("Insert into %s (%s,%s) VALUES(?,?)"
      , VideoSeries.TABLE_NAME
      , VideoSeries.VIDEO_ID_COLUMN_NAME
      , VideoSeries.SERIES_ID_COLUMN_NAME);
  private static final String REMOVE_VIDEO_FROM_SERIES_REQUEST = String.format("Delete from %s where %s = ?"
      , VideoSeries.TABLE_NAME
      , VideoSeries.ID_COLUMN_NAME);
  private static final String GET_SERIES_FOR_VIDEO = String.format("Select s.* from %s as vs "
          + "join %s as s on s.%s = vs.%s "
          + "where vs.%s = ?"
      , VideoSeries.TABLE_NAME
      , Series.TABLE_NAME, Series.ID_COLUMN_NAME, VideoSeries.SERIES_ID_COLUMN_NAME
      , VideoSeries.VIDEO_ID_COLUMN_NAME
  );
  private static final String GET_VIDEOS_FOR_SERIES = String.format("Select * from %s where %s = ?"
      , VideoSeries.TABLE_NAME
      , VideoSeries.SERIES_ID_COLUMN_NAME);
  private static final String GET_ALL_VIDEO_SERIES = String.format("Select * from %s", VideoSeries.TABLE_NAME);
  private static final String GET_SERIES_WITH_VIDEO_REQUEST = String.format("Select v.*, s.* from %s as vs "
          + "join %s as v on v.%s = vs.%s "
          + "join %s as s on s.%s = vs.%s "
          + "order by v.%s desc",
      VideoSeries.TABLE_NAME
      , VideoRepository.TABLE_NAME, VideoRepository.ID_COLUMN_NAME, VideoSeries.VIDEO_ID_COLUMN_NAME
      , Series.TABLE_NAME, Series.ID_COLUMN_NAME, VideoSeries.SERIES_ID_COLUMN_NAME
      , VideoRepository.PUBLISH_DATE_COLUMN_NAME);
  private static final String GET_VIDEO_SERIES_NAME = String.format(
      "Select s.%s "
          + "from %s as vs "
          + "join %s as s on s.%s = vs.%s "
          + "where %s = ?"
      , Series.TITLE_COLUMN_NAME
      , VideoSeries.TABLE_NAME
      , Series.TABLE_NAME, Series.ID_COLUMN_NAME, VideoSeries.SERIES_ID_COLUMN_NAME
      , VideoSeries.VIDEO_ID_COLUMN_NAME);

  private ConnectionPoolManager connectionPoolManager;
  private Connection connection;

  private PreparedStatement addVideoToSeriesStatement;
  private PreparedStatement removeVideoFromSeriesStatement;
  private PreparedStatement getVideosFromSeriesStatement;
  private PreparedStatement getAllVideosSeriesStatement;
  private PreparedStatement getSeriesWithVideosStatement;
  private PreparedStatement getVideoSeriesStatement;
  private PreparedStatement getSeriesForVideoStatment;

  public VideoSeriesRepository(ConnectionPoolManager connectionPoolManager) throws SQLException {
    this.connectionPoolManager = connectionPoolManager;
    this.initConnection();
  }

  private void connectionCheck() throws SQLException {
    if (this.connection.isClosed()) {
      this.initConnection();
    }
  }

  private void initConnection() throws SQLException {
    this.connection = this.connectionPoolManager.getConnection().getConnection();
    this.initStatements();
  }

  private void initStatements() throws SQLException {
    this.addVideoToSeriesStatement = this.connection.prepareStatement(ADD_VIDEO_TO_SERIES_REQUEST);
    this.removeVideoFromSeriesStatement = this.connection.prepareStatement(REMOVE_VIDEO_FROM_SERIES_REQUEST);
    this.getVideosFromSeriesStatement = this.connection.prepareStatement(GET_VIDEOS_FOR_SERIES);
    this.getAllVideosSeriesStatement = this.connection.prepareStatement(GET_ALL_VIDEO_SERIES);
    this.getSeriesWithVideosStatement = this.connection.prepareStatement(GET_SERIES_WITH_VIDEO_REQUEST);
    this.getVideoSeriesStatement = this.connection.prepareStatement(GET_VIDEO_SERIES_NAME);
    this.getSeriesForVideoStatment = this.connection.prepareStatement(GET_SERIES_FOR_VIDEO);
  }

  public boolean addVideoToSeries(VideoSeries videosSeries) throws SQLException {
    connectionCheck();
    this.addVideoToSeriesStatement.setInt(1, videosSeries.videoId);
    this.addVideoToSeriesStatement.setInt(2, videosSeries.seriesId);
    int numRowsUpdate = this.addVideoToSeriesStatement.executeUpdate();
    return numRowsUpdate == 1;
  }

  public boolean removeVideoFromSeries(VideoSeries videoSeries) throws SQLException {
    connectionCheck();
    this.removeVideoFromSeriesStatement.setInt(1, videoSeries.id);
    int numRowsUpdated = this.removeVideoFromSeriesStatement.executeUpdate();
    return numRowsUpdated == 0;
  }

  public List<VideoSeries> getVideoForSeries(int seriesId) throws SQLException {
    connectionCheck();
    List<VideoSeries> videos = new ArrayList<>();
    this.getVideosFromSeriesStatement.setInt(1, seriesId);
    ResultSet rs = this.getVideosFromSeriesStatement.executeQuery();
    while (rs.next()) {
      videos.add(new VideoSeries(rs));
    }
    return videos;
  }

  public List<VideoSeries> getAllVideoSeries() throws SQLException {
    connectionCheck();
    List<VideoSeries> videoSeries = new ArrayList<>();
    ResultSet resultSet = this.getAllVideosSeriesStatement.executeQuery();
    while (resultSet.next()) {
      videoSeries.add(new VideoSeries(resultSet));
    }
    return videoSeries;
  }

  public List<SeriesWithVideos> getSeriesWithVideos(int numberOfEpisodes) throws SQLException {
    connectionCheck();
    List<SeriesWithVideos> seriesWithVideosList = new ArrayList<>();
    HashMap<Integer, SeriesWithVideos> seriesWithVideosHashMap = new HashMap<>();
    ResultSet rs = this.getSeriesWithVideosStatement.executeQuery();
    while (rs.next()) {
      Video video = new Video(rs);
      Series series = new Series(rs, "s.");
      SeriesWithVideos seriesWithVideos = new SeriesWithVideos(series);
      SeriesWithVideos correspondingSeriesWithVideos = seriesWithVideosHashMap.get(series.id);
      if (correspondingSeriesWithVideos == null) {
        seriesWithVideos.videos.add(video);
        seriesWithVideosHashMap.put(series.id, seriesWithVideos);
        seriesWithVideosList.add(seriesWithVideos);
      } else if (correspondingSeriesWithVideos.videos.size() <= numberOfEpisodes) {
        correspondingSeriesWithVideos.videos.add(video);
      }
    }
    return seriesWithVideosList;
  }

  public synchronized  List<String> getVideoSeriesNames(Video video) throws SQLException {
    connectionCheck();
    List<String> seriesNames = new ArrayList<>();
    this.getVideoSeriesStatement.setInt(1, video.id);
    ResultSet rs = this.getVideoSeriesStatement.executeQuery();
    while (rs.next()) {
      seriesNames.add(rs.getString(Series.TITLE_COLUMN_NAME));
    }
    return seriesNames;
  }

  public synchronized  List<Series> getSeriesForVideo(int id) throws SQLException {
    connectionCheck();
    List<Series> series = new ArrayList<>();
    this.getSeriesForVideoStatment.setInt(1, id);
    ResultSet rs = this.getSeriesForVideoStatment.executeQuery();
    while (rs.next()) {
      series.add(new Series(rs));
    }
    return series;
  }
}
