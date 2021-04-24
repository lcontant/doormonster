package API.Util.Repositories;

import static API.databases.tables.Podcast.PODCAST;
import static API.databases.tables.Podcastepisode.*;

import API.Model.PodCastEpisode;
import API.Util.SQLConnector.ConnectionManager;
import API.Util.SQLConnector.ConnectionPoolManager;
import API.Util.SQLConnector.DSLContextUtil;
import API.databases.tables.Podcast;
import java.time.Instant;
import java.time.ZoneId;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;


import javax.naming.CommunicationException;
import java.sql.*;
import java.text.ParseException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
public class PodcastEpisodeRepository {

    private final static String TABLE_NAME = "podcastepisode";
    private final static String PODCAST_COLUMN = "podcast";
    private final static String EPISODE_NUM_COLUMN = "episodeNum";
    private final static String EPISODE_NAME_COLUMN = "episodeName";
    private final static String EPISODE_LINK_COLUMN = "episodeLink";
    private final static String EPISODE_THUMBNAIL_COLUMN = "episodeThumbnail";
    private final static String EPISODE_PUBLISH_DATE_COLUMN = "episodePublishDate";
    private final static String EPISODE_DESCRIPTION_COLUMN = "episodeDescription";
    private final static String EPISODE_LENGHT_COLUMN = "episodeLength";
    private final static String EPISODE_ID_COLUMN = "episodeID";
    private final static String EPISODE_KEYWORDS_COLUMN = "episodeKeywords";
    private final static String INSERT_PODCAST_EPISODE_REQUEST = String.format("insert into %s (%s, %s,%s,%s,%s,%s,%s,%s,%s) VALUES(?,?,?,?,?,?,?,?,?)",
            TABLE_NAME
            , PODCAST_COLUMN
            , EPISODE_NUM_COLUMN
            , EPISODE_NAME_COLUMN
            , EPISODE_LINK_COLUMN
            , EPISODE_THUMBNAIL_COLUMN
            , EPISODE_PUBLISH_DATE_COLUMN
            , EPISODE_DESCRIPTION_COLUMN
            , EPISODE_LENGHT_COLUMN
            , EPISODE_KEYWORDS_COLUMN);
    private final static String UPDATE_PODCAST_REQUEST = String.format("Update %s set " +
            "%s = ?" +
            ", %s = ?" +
            ", %s = ?" +
            ", %s = ?" +
            ", %s = ?" +
            ", %s = ?" +
            ", %s = ?" +
            ", %s = ?" +
            ", %s = ?" +
            "where %s = ?",
            TABLE_NAME
            , PODCAST_COLUMN
            , EPISODE_NUM_COLUMN
            , EPISODE_NAME_COLUMN
            , EPISODE_LINK_COLUMN
            , EPISODE_THUMBNAIL_COLUMN
            , EPISODE_PUBLISH_DATE_COLUMN
            , EPISODE_DESCRIPTION_COLUMN
            , EPISODE_LENGHT_COLUMN
            , EPISODE_KEYWORDS_COLUMN
            , EPISODE_ID_COLUMN);



    ConnectionPoolManager connectionPoolManager;
    Connection connection;
    ConnectionManager connectionManager;
    DSLContext dslContext;
    int retries = 0;

    private PreparedStatement insertPodcastEpisodeStatement;
    private PreparedStatement updatePodcastStatement;

    public PodcastEpisodeRepository(ConnectionManager connectionManager, ConnectionPoolManager connectionPoolManager) throws SQLException {
        this.connectionPoolManager = connectionPoolManager;
        this.connectionManager = connectionManager;
        this.retries = 0;
        this.initConnection();
    }

    private void initConnection() throws SQLException {
        this.retries ++;
        this.connection = this.connectionPoolManager.getConnection().connection;
        this.dslContext = DSLContextUtil.getContext(this.connection);
        try {
            this.dslContext.selectFrom(PODCASTEPISODE);
        } catch (Exception e) {
            if (this.retries < 3) {
                initConnection();
            }
            e.printStackTrace();
        }
        this.retries = 0;
        this.initStatements();
    }

    private void initStatements() throws SQLException {
        this.insertPodcastEpisodeStatement = this.connection.prepareStatement(INSERT_PODCAST_EPISODE_REQUEST);
        this.updatePodcastStatement = this.connectionManager.prepareStatement(UPDATE_PODCAST_REQUEST);
    }

    public synchronized List<PodCastEpisode> getList() throws SQLException, ParseException {
        String request = "Select * from  podcastepisode";
        ResultSet rs = this.connectionManager.query(request);
        List<PodCastEpisode> podCastEpisodes = new ArrayList<>();
        while (rs.next()) {
            podCastEpisodes.add(new PodCastEpisode(rs));
        }

        return podCastEpisodes;
    }

    public synchronized List<PodCastEpisode> getPublishedList() {
        return this.dslContext.selectFrom(PODCASTEPISODE)
            .where(PODCASTEPISODE.EPISODEPUBLISHDATE.lessOrEqual(new Date(Instant.now().toEpochMilli()))).fetchInto(PodCastEpisode.class);
    }


    public synchronized PodCastEpisode getById(String id) throws SQLException, ParseException{
        String request = "Select * from podcastepisode where episodeID = " + id;
        ResultSet rs = this.connectionManager.query(request);
        PodCastEpisode podCastEpisode = null;
        if (rs.next()) {
            podCastEpisode = new PodCastEpisode(rs);
        }
        return podCastEpisode;
    }

    public synchronized int getEpisodeCount(String title) throws  SQLException {
        String request = "SELECT COUNT(*) as episodeCount FROM podcastepisode WHERE podcast=" + title;
        int count = -1;
        ResultSet rs = this.connectionManager.query(request);
        if (rs.next()){
            count = Integer.parseInt(rs.getString("episodeCount"));
        }
        return count;
    }

    public  List<PodCastEpisode> getEpisodesFor(String title) {
        return this.dslContext.selectFrom(PODCASTEPISODE)
            .where(PODCASTEPISODE.PODCAST.eq(title).and(PODCASTEPISODE.EPISODEPUBLISHDATE.lessOrEqual(new Date(Instant.now().toEpochMilli()))))
            .fetchInto(PodCastEpisode.class);
    }

    public boolean insertPodcast(PodCastEpisode podCastEpisode) throws SQLException {
        if (this.connection.isClosed()) {
           this.initConnection();
        }
        boolean updateSuccessfull = false;
        int numRowsUpdate = this.dslContext.insertInto(PODCASTEPISODE
        , PODCASTEPISODE.PODCAST, PODCASTEPISODE.EPISODENUM, PODCASTEPISODE.EPISODENAME, PODCASTEPISODE.EPISODELINK, PODCASTEPISODE.EPISODETHUMBNAIL, PODCASTEPISODE.EPISODEPUBLISHDATE, PODCASTEPISODE.EPISODEDESCRIPTION, PODCASTEPISODE.EPISODELENGTH, PODCASTEPISODE.EPISODEKEYWORDS)
            .values(podCastEpisode.podcast, podCastEpisode.episodeNum, podCastEpisode.episodeName, podCastEpisode.episodeLink, podCastEpisode.episodeThumbnail, new Date(podCastEpisode.episodePublishDate.toInstant(ZoneOffset.of("-06:00")).toEpochMilli()), podCastEpisode.episodeDescription, podCastEpisode.episodeLength, podCastEpisode.episodeKeywords)
            .execute();
        return numRowsUpdate == 1;
    }

    public boolean updatePodcastEpisode(PodCastEpisode episode) throws SQLException {
       if (this.connection.isClosed()) {
          this.initConnection();
       }
       boolean updateSuccessfull = false;
        this.updatePodcastStatement.setString(1, episode.podcast);
        this.updatePodcastStatement.setInt(2, episode.episodeNum);
        this.updatePodcastStatement.setString(3, episode.episodeName);
        this.updatePodcastStatement.setString(4, episode.episodeLink);
        this.updatePodcastStatement.setString(5, episode.episodeThumbnail);
        this.updatePodcastStatement.setDate(6, new Date(episode.episodePublishDate.toInstant(ZoneOffset.UTC).toEpochMilli()));
        this.updatePodcastStatement.setString(7, episode.episodeDescription);
        this.updatePodcastStatement.setString(8, episode.episodeLength);
        this.updatePodcastStatement.setString(9, episode.episodeKeywords);
        this.updatePodcastStatement.setInt(10, episode.episodeID);
        return this.updatePodcastStatement.executeUpdate() == 1;
    }
}
