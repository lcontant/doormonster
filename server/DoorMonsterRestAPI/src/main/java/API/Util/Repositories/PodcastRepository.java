package API.Util.Repositories;



import static API.databases.tables.Podcast.*;

import API.Model.DBConnection;
import API.Model.Podcast;
import API.Util.SQLConnector.ConnectionManager;
import API.Util.SQLConnector.ConnectionPoolManager;
import API.Util.SQLConnector.DSLContextUtil;
import API.databases.tables.Podcastepisode;
import java.sql.Date;
import java.time.Instant;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Component
public class PodcastRepository {

    ConnectionManager connectionManager;
    ConnectionPoolManager connectionPoolManager;
    DBConnection dbConnection;
    DSLContext dslConnection;
    Connection connection;
    private static String TITLE_COLUMN = "podcastTitle";
    private static String TABLE_NAME = "podcast";
    private static String SUPPORTER_ONLY_COLUMN = "supporterOnly";
    private static String BASE_PODCAST_REQUEST = String.format("Select p.*, COUNT(pe.episodeNum) as episodeCount from podcast as p LEFT JOIN podcastepisode as pe ON pe.podcast = p.podcastTitle ");
    private static String GET_PODCAST_BY_TITLE_REQUEST = String.format("%s where p.%s = ? group by p.podcastID", BASE_PODCAST_REQUEST, TITLE_COLUMN);
    private static String GET_ALL_PODCAST_REQUEST = String.format("%s where p.%s=0 group by p.podcastID", BASE_PODCAST_REQUEST, SUPPORTER_ONLY_COLUMN);
    private static String GET_ALL_PODCAST_INCLUDING_SUPPORTER_ONLY_REQUEST = String.format("%s group by p.podcastID", BASE_PODCAST_REQUEST);

    private PreparedStatement getAllPodcastsStatement;
    private PreparedStatement getPodcastByTitleStatement;
    private PreparedStatement getAllPodcastsIncludingSupporterOnlyStatement;

    public PodcastRepository(ConnectionManager connectionManager, ConnectionPoolManager connectionPoolManager) throws SQLException {
        this.connectionManager = connectionManager;
        initConnection(connectionPoolManager);
    }

    private void initConnection(ConnectionPoolManager connectionPoolManager) throws SQLException {
        this.connectionPoolManager = connectionPoolManager;
        this.dbConnection = this.connectionPoolManager.getConnection();
        this.connection = this.dbConnection.getConnection();
        this.dslConnection = DSLContextUtil.getContext(this.connection);
        this.prepareStatements();
    }

    private void prepareStatements() throws SQLException {
        getAllPodcastsStatement = this.connection.prepareStatement(GET_ALL_PODCAST_REQUEST);
        getPodcastByTitleStatement = this.connection.prepareStatement(GET_PODCAST_BY_TITLE_REQUEST);
        getAllPodcastsIncludingSupporterOnlyStatement = this.connection.prepareStatement(GET_ALL_PODCAST_INCLUDING_SUPPORTER_ONLY_REQUEST);
    }

    public synchronized List<Podcast> getList() throws SQLException {
        if (this.connection.isClosed()) {
           this.initConnection(this.connectionPoolManager);
        }
        List<Podcast> podcasts = new ArrayList<>();
        ResultSet rs = this.getAllPodcastsStatement.executeQuery();
        while (rs.next()) {
            podcasts.add(new Podcast(rs));
        }
        return podcasts;
    }

    public Podcast getByTitle(String title) throws SQLException {
        if (this.connection.isClosed()) {
            this.initConnection(this.connectionPoolManager);
        }
        Podcast podcast = null;
        this.getPodcastByTitleStatement.setString(1, title);
        ResultSet rs = this.getPodcastByTitleStatement.executeQuery();
        if (rs.next()) {
            podcast = new Podcast(rs);
        }
        return podcast;
    }

    public synchronized Podcast getById(String id) throws SQLException {
        String request = "SELECT p.*, COUNT(pe.episodeNum) as episodeCount" +
                "  FROM podcast as p\n" +
                "JOIN podcastepisode as pe ON pe.podcast = p.podcastTitle\n" +
                "   Where podcastID = " + id +
                "\ngroup by pe.podcast";
        ResultSet rs = this.connectionManager.query(request);
        Podcast podcast = null;
        if (rs.next()){
            podcast = new Podcast(rs);
        }
        return podcast;
    }

    public List<Podcast> getAllSeries() {
       return this.dslConnection.selectFrom(PODCAST).fetchInto(Podcast.class);
    }

    public List<Podcast> getAllSeriesWithPublishedEpisodes() {
        return this.dslConnection.selectDistinct(PODCAST.asterisk()).from(PODCAST).join(Podcastepisode.PODCASTEPISODE).on(Podcastepisode.PODCASTEPISODE.PODCAST.eq(PODCAST.PODCASTTITLE))
            .where(PODCAST.SUPPORTERONLY.eq(false)).and(Podcastepisode.PODCASTEPISODE.EPISODEPUBLISHDATE.lessOrEqual(new Date(Instant.now().toEpochMilli())))
            .fetchInto(Podcast.class);
    }

    public List<Podcast> getAllSeriesIncludingSupporterOnly()  {
        return this.dslConnection.selectDistinct(PODCAST.asterisk()).from(PODCAST).join(Podcastepisode.PODCASTEPISODE).on(Podcastepisode.PODCASTEPISODE.PODCAST.eq(PODCAST.PODCASTTITLE))
            .where(Podcastepisode.PODCASTEPISODE.EPISODEPUBLISHDATE.lessOrEqual(new Date(Instant.now().toEpochMilli())))
            .fetchInto(Podcast.class);
    }

    public boolean createNewPodcast(Podcast podcast) {
        int numberOfInsertedRecords = this.dslConnection.insertInto(PODCAST, PODCAST.PODCASTTITLE, PODCAST.PODCASTSUMMARY, PODCAST.THUMBNAILPATH,PODCAST.SUPPORTERONLY)
            .values(podcast.podcastTitle, podcast.podcastSummary, podcast.thumbnailPath, podcast.supporterOnly).execute();
        return numberOfInsertedRecords == 1;
    }


}
