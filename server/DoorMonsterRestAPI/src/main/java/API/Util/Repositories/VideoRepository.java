package API.Util.Repositories;

import static API.databases.Tables.COMMENTS;
import static API.databases.Tables.TAGS;
import static API.databases.Tables.VIDEOSERIES;
import static API.databases.tables.Video.VIDEO;

import API.Model.DBConnection;
import API.Model.Series;
import API.Model.Tag;
import API.Model.Video;
import API.Model.VideoSeries;
import API.Util.SQLConnector.ConnectionPoolManager;
import API.Util.SQLConnector.DSLContextUtil;
import API.databases.Tables;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.*;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class VideoRepository {

  private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
  public static String COMMENTS_TABLE_NAME = "comments";
  public static String COMMENTS_ID_COLUMN_NAME = "commentid";

  public static String TABLE_NAME = "video";
  public static String PUBLISH_DATE_COLUMN_NAME = "videoPublishDate";
  public static String ID_COLUMN_NAME = "id";
  public static String VIEWS_COLUMN_NAME = "views";
  public static String VIMEO_ID_COLUMN_NAME = "videoID";
  public static String VIDEO_TITLE_COLUM_NAME = "VideoTitle";
  public static String VIDEO_CATEGORY_COLUMN_NAME = "VideoCategory";
  public static String VIDEO_THUMBNAIL_COLUMN_NAME = "videoThumbnail";
  public static String VIDEO_SUMMARY_COLUMN_NAME = "videoSummary";
  public static String VIDEO_KEYWORDS_COLUMN_NAME = "videoKeywords";
  public static String VIDEO_PUBLISHED_COLUMN_NAME = "videoPublished";
  public static String VIDEO_FILE_LINK_COLUMN_NAME = "videoFileLink";


  private ConnectionPoolManager poolManager;
  private DBConnection dbConnection;
  private DSLContext create;
  private Connection mainConnection;
  private PreparedStatement getVideoByIdStatement;
  private PreparedStatement getAllVideosStatement;
  private PreparedStatement addViewStatement;
  private PreparedStatement getByIdStatement;
  private PreparedStatement getFeaturedStatement;
  private PreparedStatement getLatestStatement;
  private PreparedStatement searchStatement;
  private PreparedStatement getVideoForSeriesStatement;
  private PreparedStatement uploadVideoStatement;
  private PreparedStatement getVideoSeriesStatement;
  private PreparedStatement getAllVideoCategoriesStatement;
  private PreparedStatement publishVideoStatement;
  private PreparedStatement getAllpublishedVideosStatement;
  private PreparedStatement updateVideoStatement;
  private PreparedStatement deleteVideoStatement;
  private PreparedStatement insertTagStatement;
  private PreparedStatement getVideoTagStatement;
  private PreparedStatement getVideoByTitleStatement;
  private PreparedStatement setVideoFileLinkStatement;

  public VideoRepository(
      ConnectionPoolManager connectionPoolManager) throws SQLException {
    this.poolManager = connectionPoolManager;
    this.initConnection();
  }

  private void initConnection() throws SQLException {
    this.dbConnection = this.poolManager.getConnection();
    this.mainConnection = dbConnection.getConnection();
    this.create = DSLContextUtil.getContext(this.mainConnection);
    prepareStatementsWithConnection();
  }

  private void connectionCheck() throws SQLException {
    if (this.mainConnection.isClosed()) {
      this.initConnection();
    }
  }

  private void prepareStatementsWithConnection() throws SQLException {
    initGetAllVideosStatement();
    initAddViewToVideoStatement();
    initSearchStatement();
    initGetByIdStatement();
    initVideosForSeriesStatement();
    initGetLatestVideoStatement();
    initUploadVideoStatement();
    initGetVideoSeriesStatement();
    initGetVideoCategoriesStatement();
    initPublishVideoStatement();
    initUpdateVideoStatement();
    initDeleteVideoStatement();
    initGetVideoTagStatement();
    initInsertTagVideoStatement();
    initGetVideoByTitleStatement();
    initSetVideoFileLinkStatement();
    initGetVideoByIdStatement();
  }

  private void initSetVideoFileLinkStatement() throws SQLException {
    String request = String.format("Update %s set %s = ? where %s = ?", TABLE_NAME, VIDEO_FILE_LINK_COLUMN_NAME, ID_COLUMN_NAME);
    this.setVideoFileLinkStatement = this.mainConnection.prepareStatement(request);
  }

  private void initGetVideoByIdStatement() throws SQLException {
    String request = String.format("Select * from %s where %s = ?", TABLE_NAME, ID_COLUMN_NAME);
    this.getVideoByIdStatement = this.mainConnection.prepareStatement(request);
  }

  private void initGetVideoByTitleStatement() throws SQLException {
    String request = String.format("Select * from %s where %s = ?", TABLE_NAME, VIDEO_TITLE_COLUM_NAME);
    this.getVideoByTitleStatement = this.mainConnection.prepareStatement(request);
  }

  private void initGetVideoTagStatement() throws SQLException {
    String request = String.format("Select * from %s where %s = ?", Tag.TABLE_NAME, Tag.VIDEO_ID_COLUMN_NAME);
    this.getVideoTagStatement = this.mainConnection.prepareStatement(request);
  }

  private void initInsertTagVideoStatement() throws SQLException {
    String request = String.format("Insert into %s (%s,%s,%s) VALUES(?,?,?)", Tag.TABLE_NAME, Tag.VALUE_COLUMN_NAME, Tag.VIDEO_ID_COLUMN_NAME, Tag.WEIGHT_COLUMN_NAME);
    this.insertTagStatement = this.mainConnection.prepareStatement(request);
  }

  private void initGetAllVideosStatement() throws SQLException {
    String request = String.format("SELECT * from %s order BY %s desc", TABLE_NAME, PUBLISH_DATE_COLUMN_NAME);
    this.getAllVideosStatement = this.mainConnection.prepareStatement(request);
  }


  private void initAddViewToVideoStatement() throws SQLException {
    String request = String.format("Update %s set %s = %s + 1 where %s = ?", TABLE_NAME, VIEWS_COLUMN_NAME, VIEWS_COLUMN_NAME, VIMEO_ID_COLUMN_NAME);
    this.addViewStatement = this.mainConnection.prepareStatement(request);
  }

  private void initSearchStatement() throws SQLException {
    String request = String.format("Select\tv.* FROM\n" +
            "%s as v\n" +
            "join %s as t on t.%s = v.%s\n" +
            "where \n" +
            "t.%s LIKE ? OR\n" +
            "SOUNDEX(v.%s) = SOUNDEX(?)\n" +
            "GROUP BY v.%s\n" +
            "order by SUM(t.%s) desc;"
        , TABLE_NAME
        , Tag.TABLE_NAME, Tag.VIDEO_ID_COLUMN_NAME, ID_COLUMN_NAME
        , Tag.VALUE_COLUMN_NAME
        , VIDEO_TITLE_COLUM_NAME
        , ID_COLUMN_NAME
        , Tag.WEIGHT_COLUMN_NAME);
    this.searchStatement = this.mainConnection.prepareStatement(request);
  }

  private void initGetLatestVideoStatement() throws SQLException {
    String request = String.format("SELECT * FROM %s WHERE %s <= ? ORDER BY %s DESC LIMIT ?", TABLE_NAME, PUBLISH_DATE_COLUMN_NAME, PUBLISH_DATE_COLUMN_NAME);
    this.getLatestStatement = this.mainConnection.prepareStatement(request);
  }

  private void initGetByIdStatement() throws SQLException {
    String request = String.format("Select * from %s where %s = ?", TABLE_NAME, VIMEO_ID_COLUMN_NAME);
    Connection connection = this.poolManager.getConnection().getConnection();
    this.getByIdStatement = connection.prepareStatement(request);
  }

  private void initVideosForSeriesStatement() throws SQLException {
    String request = String.format("Select v.*, count(c.%s) as commentCount \n"
            + "from %s as v\n"
            + "left join %s as c on c.%s = v.%s "
            + "join %s as vs on vs.%s = v.%s "
            + "join %s as s on s.%s = vs.%s "
            + "where s.%s = ? and %s <= CURDATE() "
            + "Group by v.%s ORDER BY %s Desc"
        , COMMENTS_ID_COLUMN_NAME
        , TABLE_NAME
        , COMMENTS_TABLE_NAME, CommentRepository.MEDIA_ID_COLUMN_NAME, VIMEO_ID_COLUMN_NAME
        , VideoSeries.TABLE_NAME, VideoSeries.VIDEO_ID_COLUMN_NAME, ID_COLUMN_NAME
        , Series.TABLE_NAME, Series.ID_COLUMN_NAME, VideoSeries.SERIES_ID_COLUMN_NAME
        , Series.TITLE_COLUMN_NAME, PUBLISH_DATE_COLUMN_NAME
        , ID_COLUMN_NAME, PUBLISH_DATE_COLUMN_NAME);
    this.getVideoForSeriesStatement = this.mainConnection.prepareStatement(request);
  }

  private void initUploadVideoStatement() throws SQLException {
    String request = String.format("INSERT INTO %s (%s,%s,  %s, %s, %s, %s, %s, %s, %s) values (?,?,?,?,?,?,?,?,?)", TABLE_NAME
        , VIMEO_ID_COLUMN_NAME, VIDEO_TITLE_COLUM_NAME, VIDEO_CATEGORY_COLUMN_NAME, VIDEO_THUMBNAIL_COLUMN_NAME, PUBLISH_DATE_COLUMN_NAME, VIDEO_SUMMARY_COLUMN_NAME, VIDEO_KEYWORDS_COLUMN_NAME, VIEWS_COLUMN_NAME, VIDEO_FILE_LINK_COLUMN_NAME);
    uploadVideoStatement = this.mainConnection.prepareStatement(request);
  }

  private void initGetVideoSeriesStatement() throws SQLException {
    String request = String.format("Select distinct(%s) from %s", Series.TITLE_COLUMN_NAME, Series.TABLE_NAME);
    getVideoSeriesStatement = this.mainConnection.prepareStatement(request);
  }

  private void initGetVideoCategoriesStatement() throws SQLException {
    String request = String.format("Select distinct(%s) from %s", VIDEO_CATEGORY_COLUMN_NAME, TABLE_NAME);
    getAllVideoCategoriesStatement = this.mainConnection.prepareStatement(request);
  }

  private void initPublishVideoStatement() throws SQLException {
    String request = String.format("Update %s set %s = 1 where %s = ?", TABLE_NAME, VIDEO_PUBLISHED_COLUMN_NAME, VIMEO_ID_COLUMN_NAME);
    publishVideoStatement = this.mainConnection.prepareStatement(request);
  }

  private void initGetAllPublishedVideosStatement() throws SQLException {
    String request = String.format("Select * from %s where %s =1", TABLE_NAME, VIDEO_PUBLISHED_COLUMN_NAME);
    getAllpublishedVideosStatement = this.mainConnection.prepareStatement(request);
  }

  private void initUpdateVideoStatement() throws SQLException {
    String request = String.format("Update %s set" +
        " %s = ?" +
        ", %s =?" +
        ", %s = ?" +
        ", %s = ?" +
        ", %s = ?" +
        ", %s = ?" +
        ", %s = ?" +
        " where %s = ?", TABLE_NAME, VIMEO_ID_COLUMN_NAME, VIDEO_TITLE_COLUM_NAME, VIDEO_CATEGORY_COLUMN_NAME, VIDEO_THUMBNAIL_COLUMN_NAME, PUBLISH_DATE_COLUMN_NAME, VIDEO_SUMMARY_COLUMN_NAME, VIDEO_KEYWORDS_COLUMN_NAME, ID_COLUMN_NAME);
    updateVideoStatement = this.mainConnection.prepareStatement(request);
  }

  private void initDeleteVideoStatement() throws SQLException {
    String request = String.format("DELETE from %s where %s = ?", TABLE_NAME, ID_COLUMN_NAME);
    deleteVideoStatement = this.mainConnection.prepareStatement(request);
  }


  private synchronized List<Video> handleListResultSet(PreparedStatement statement) throws SQLException {
    connectionCheck();
    ResultSet rs = statement.executeQuery();
    List<Video> videos = new ArrayList<>();
    while (rs.next()) {
      videos.add(new Video(rs));
    }
    return videos;
  }

  private synchronized List<Video> handlePublishedResultSet(PreparedStatement statement) throws SQLException {
    connectionCheck();
    ResultSet rs = statement.executeQuery();
    List<Video> videos = new ArrayList<>();
    while (rs.next()) {
      if (rs.getBoolean(VIDEO_PUBLISHED_COLUMN_NAME)) {
        videos.add(new Video(rs));
      }
    }
    return videos;
  }

  private synchronized Video handleSingleResultSet(PreparedStatement statement) throws SQLException {
    ResultSet rs = statement.executeQuery();
    Video video = null;
    if (rs.next()) {
      video = new Video(rs);
    }
    return video;
  }

  private synchronized Video handleSinglePublishedResultSet(PreparedStatement statement) throws SQLException {
    ResultSet rs = statement.executeQuery();
    Video video = null;
    if (rs.next() && rs.getBoolean(VIDEO_PUBLISHED_COLUMN_NAME)) {
      video = new Video(rs);
    }
    return video;
  }

  public synchronized List<Video> getAllVideos() throws SQLException {
    connectionCheck();
    return this.handleListResultSet(this.getAllVideosStatement);
  }

  public synchronized List<Video> getAllPublishedVideos() throws SQLException {
    connectionCheck();
    return this.handlePublishedResultSet(this.getAllVideosStatement);
  }

  public synchronized Video getFeatured() throws SQLException {
    connectionCheck();
    getFeaturedStatement.setString(1, "Community comments");
    getFeaturedStatement.setString(2, "Behind the Scenes");
    getFeaturedStatement.setString(3, "Altered Egos");
    return handleSinglePublishedResultSet(getFeaturedStatement);
  }

  public synchronized boolean addViewToVideo(String id) throws SQLException {
    connectionCheck();
    int numOfRowsAffected = 0;
    try {
      addViewStatement.setString(1, id);
      numOfRowsAffected = addViewStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return numOfRowsAffected == 1;
  }

  public synchronized List<Video> search(String query) throws SQLException {
    connectionCheck();
    List<Video> videos = new ArrayList<>();
    String[] queries = query.split(" ");
    String currentQuery = "";
    for (int i = 0; i < queries.length; i++) {
      currentQuery = '%' + queries[i] + '%';
      searchStatement.setString(1, currentQuery);
      searchStatement.setString(2, queries[i]);
      videos.addAll(handleListResultSet(searchStatement));
    }
    return videos;
  }


  public List<Video> getLatestVideos(int videoNumber) throws SQLException {
    connectionCheck();
    return this.create.selectFrom(VIDEO)
        .where(VIDEO.VIDEOPUBLISHDATE.lt(new Timestamp(Instant.now().toEpochMilli())))
        .orderBy(VIDEO.VIDEOPUBLISHDATE.desc())
        .limit(videoNumber)
        .fetchInto(Video.class);
  }

  public synchronized Video getById(String Id) throws SQLException {
    connectionCheck();
    getByIdStatement.setString(1, Id);
    Video video = null;
    getByIdStatement.execute();
    ResultSet rs = getByIdStatement.getResultSet();
    if (rs.next()) {
      video = new Video(rs);
    }
    return video;
  }


  public synchronized List<Video> getVideosForSeries(String title) throws SQLException {
    connectionCheck();
    List<Video> videos = new ArrayList<>();
    getVideoForSeriesStatement.setString(1, title);
    return handlePublishedResultSet(getVideoForSeriesStatement);
  }

  public synchronized boolean uploadVideo(Video video) throws SQLException {
    connectionCheck();
    int rowsUpdated = 0;
    try {
      uploadVideoStatement.setString(1, video.videoID);
      uploadVideoStatement.setString(2, video.videoTitle);
      uploadVideoStatement.setString(3, video.videoCategory);
      uploadVideoStatement.setString(4, video.videoThumbnail);
      uploadVideoStatement.setDate(5, new Date(video.videoPublishDate.toInstant(ZoneOffset.of("-06:00")).toEpochMilli()));
      uploadVideoStatement.setString(6, video.videoSummary);
      uploadVideoStatement.setString(7, video.videoKeyWords);
      uploadVideoStatement.setInt(8, video.views);
      uploadVideoStatement.setString(9, video.videoFileLink);
      rowsUpdated = uploadVideoStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return rowsUpdated == 1;
  }

  //TODO: Transfer this
  public synchronized List<String> getVideoSeriesName() throws SQLException {
    connectionCheck();
    List<String> series = new Vector<>();
    try {
      ResultSet rs = getVideoSeriesStatement.executeQuery();
      while (rs.next()) {
        series.add(rs.getString(Series.TITLE_COLUMN_NAME));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return series;
  }

  public synchronized List<String> getVideoCategoriesNames() throws SQLException {
    connectionCheck();
    List<String> categories = new Vector<>();
    try {
      ResultSet rs = getAllVideoCategoriesStatement.executeQuery();
      while (rs.next()) {
        categories.add(rs.getString(VIDEO_CATEGORY_COLUMN_NAME));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return categories;
  }

  public synchronized boolean publishVideo(String id) throws SQLException {
    connectionCheck();
    boolean succesfull = false;
    int rowsUpdated = 0;
    try {
      this.publishVideoStatement.setString(1, id);
      rowsUpdated = this.publishVideoStatement.executeUpdate();
      succesfull = rowsUpdated == 1;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return succesfull;
  }

  public synchronized boolean transferCommentIds(String vimeoId,int videoId) {
     int rowsUpdated = this.create.update(COMMENTS).set(COMMENTS.MEDIAID, String.valueOf(videoId)).where(COMMENTS.MEDIAID.eq(vimeoId)).execute();
     return rowsUpdated > 0;
  }

  public synchronized boolean updateVideo(Video video) throws SQLException {
    connectionCheck();
    int rowsUpdated = 0;
    try {
      updateVideoStatement.setString(1, video.videoID);
      updateVideoStatement.setString(2, video.videoTitle);
      updateVideoStatement.setString(3, video.videoCategory);
      updateVideoStatement.setString(4, video.videoThumbnail);
      updateVideoStatement.setDate(5, new Date(video.videoPublishDate.toInstant(ZoneOffset.UTC).toEpochMilli()));
      updateVideoStatement.setString(6, video.videoSummary);
      updateVideoStatement.setString(7, video.videoKeyWords);
      updateVideoStatement.setInt(8, video.id);
      rowsUpdated = updateVideoStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return rowsUpdated == 1;
  }

  public synchronized boolean deleteVideo(int id) throws SQLException {
    connectionCheck();
    int rowsUpdated = 0;
    String vimeoId = this.create.select(Tables.VIDEO.VIDEOID).from(Tables.VIDEO).where(Tables.VIDEO.ID.eq((long)id)).fetchInto(String.class).get(0);
    this.create.deleteFrom(COMMENTS).where(COMMENTS.MEDIAID.eq(vimeoId));
    this.create.deleteFrom(TAGS).where(TAGS.VIDEO_ID.eq((long) id)).execute();
    this.create.deleteFrom(VIDEOSERIES).where(VIDEOSERIES.VIDEO_ID.eq((long) id)).execute();
    rowsUpdated = this.create.deleteFrom(Tables.VIDEO).where(VIDEO.ID.eq((long) id)).execute();
    return rowsUpdated == 1;
  }

  public synchronized boolean insertTag(Tag tag) throws SQLException {
    connectionCheck();
    int rowsUpdated = 0;
    this.insertTagStatement.setString(1, tag.value);
    this.insertTagStatement.setInt(2, tag.videoId);
    this.insertTagStatement.setInt(3, tag.weight);
    rowsUpdated = this.insertTagStatement.executeUpdate();
    return rowsUpdated == 1;
  }


  public synchronized List<Tag> getTagsForVideo(int videoId) throws SQLException {
    connectionCheck();
    List<Tag> tags = new ArrayList<>();
    this.getVideoTagStatement.setInt(1, videoId);
    ResultSet rs = this.getVideoTagStatement.executeQuery();
    while (rs.next()) {
      tags.add(new Tag(rs));
    }
    return tags;
  }

  public synchronized Video getVideoByTitle(String title) throws SQLException {
    connectionCheck();
    Video video = null;
    this.getVideoByTitleStatement.setString(1, title);
    ResultSet rs = this.getVideoByTitleStatement.executeQuery();
    if (rs.next()) {
      video = new Video(rs);
    }
    return video;
  }

  public synchronized boolean setVideoFileLink(Video video) throws SQLException {
    connectionCheck();
    this.setVideoFileLinkStatement.setString(1, video.videoFileLink);
    this.setVideoFileLinkStatement.setInt(2, video.id);
    int numRowsUpdated = this.setVideoFileLinkStatement.executeUpdate();
    return numRowsUpdated == 1;
  }

  public synchronized Video getVideoById(int id) throws SQLException {
    connectionCheck();
    this.getVideoByIdStatement.setInt(1, id);
    ResultSet rs = this.getVideoByIdStatement.executeQuery();
    Video video = null;
    if (rs.next()) {
      video = new Video(rs);
    }
    return video;
  }



}
