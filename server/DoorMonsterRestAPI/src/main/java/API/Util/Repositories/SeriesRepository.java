package API.Util.Repositories;

import static API.databases.tables.Series.*;

import API.Model.Series;
import API.Util.SQLConnector.ConnectionPoolManager;
import API.Util.SQLConnector.DSLContextUtil;
import API.databases.tables.Video;
import API.databases.tables.Videoseries;
import java.util.Collections;
import org.jooq.AggregateFunction;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SeriesRepository {

  private ConnectionPoolManager connectionPoolManager;
  private VideoRepository videoRepository;
  private Connection connection;

  private PreparedStatement getAllSeriesStatement;
  private PreparedStatement getSeriesByTextIdStatement;
  private PreparedStatement createSeriesStatement;
  private PreparedStatement updateSeriesStatement;
  private PreparedStatement deleteSeriesStatement;

  private final String GET_SERIES_BY_TEXT_ID_REQUEST = String.format("Select * from %s where %s = ?"
      , Series.TABLE_NAME
      , Series.TEXT_ID_COLUMN_NAME);

  private final String CREATE_SERIES_REQUEST = String.format("Insert into %s (%s, %s, %s, %s) VALUES (?,?,?,?)"
      , Series.TABLE_NAME
      , Series.TITLE_COLUMN_NAME, Series.TEXT_ID_COLUMN_NAME, Series.DESCRIPTION_COLUMN_NAME, Series.CREATOR_ID_COLUMN_NAME);
  private final String UPDATE_SERIES_REQUEST = String.format("Update %s set %s = ?,  %s = ?  where %s = ?"
      , Series.TABLE_NAME
      , Series.TITLE_COLUMN_NAME
      , Series.DESCRIPTION_COLUMN_NAME
      , Series.ID_COLUMN_NAME);
  private final String GET_ALL_SERIES = String.format("Select * from %s", Series.TABLE_NAME);

  private DSLContext create;
  public SeriesRepository(ConnectionPoolManager connectionPoolManager, VideoRepository videoRepository) throws SQLException {
    this.connectionPoolManager = connectionPoolManager;
    this.videoRepository = videoRepository;
    this.initConnection();
  }

  private void initConnection() throws SQLException {
    this.connection = this.connectionPoolManager.getConnection().getConnection();
    this.create = DSLContextUtil.getContext(this.connection);
    this.initStatements();
  }

  private void initStatements() throws SQLException {
    this.getSeriesByTextIdStatement = this.connection.prepareStatement(GET_SERIES_BY_TEXT_ID_REQUEST);
    this.createSeriesStatement = this.connection.prepareStatement(CREATE_SERIES_REQUEST);
    this.updateSeriesStatement = this.connection.prepareStatement(UPDATE_SERIES_REQUEST);
    this.getAllSeriesStatement = this.connection.prepareStatement(GET_ALL_SERIES);
  }

  private void connectionCheck() throws SQLException {
    if (this.connection.isClosed()) {
      this.initConnection();
    }
  }

  public List<Series> getAllSeries() throws SQLException {
      List<Series> series = new ArrayList<>();
      ResultSet rs = this.getAllSeriesStatement.executeQuery();
      while (rs.next()) {
        series.add(new Series(rs));
      }
      return series;
  }

  public synchronized Series getSeriesByTextId(String id) throws SQLException, ParseException {
    this.connectionCheck();
    Series series = null;
    this.getSeriesByTextIdStatement.setString(1, id);
    ResultSet rs = this.getSeriesByTextIdStatement.executeQuery();
    if (rs.next()) {
      series = new Series(rs);
    }
    return series;
  }

  public boolean CreateNewSeries(Series series) {
      int count = this.create.insertInto(SERIES, SERIES.TITLE, SERIES.TEXTID, SERIES.DESCRIPTION, SERIES.CREATOR_ID)
          .values(series.title, series.textId, series.description, (long) series.creatorId).execute();
    return count == 1;
  }

  public synchronized boolean UpdateSeries(Series series) throws SQLException {
    this.connectionCheck();
    this.updateSeriesStatement.setString(1, series.title);
    this.updateSeriesStatement.setString(2, series.description);
    this.updateSeriesStatement.setInt(3, series.id);
    int numRowsUpdated = this.updateSeriesStatement.executeUpdate();
    return numRowsUpdated == 1;
  }

  public List<Series> getSeriesInOrderOfUpdate() {
     return this.create.selectDistinct(SERIES.ID, SERIES.CREATOR_ID, SERIES.TITLE, SERIES.TEXTID, SERIES.DESCRIPTION)
         .from(SERIES
          .join(Videoseries.VIDEOSERIES)
          .on(Videoseries.VIDEOSERIES.SERIES_ID.eq(SERIES.ID))
          .join(Video.VIDEO)
          .on(Video.VIDEO.ID.eq(Videoseries.VIDEOSERIES.VIDEO_ID)))
         .groupBy(SERIES.ID)
         .orderBy(DSL.max(Video.VIDEO.VIDEOPUBLISHDATE))
         .fetchInto(Series.class);

  }

}
