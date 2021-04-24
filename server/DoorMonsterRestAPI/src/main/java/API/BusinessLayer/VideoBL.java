package API.BusinessLayer;

import API.Model.Series;
import API.Model.Tag;
import API.Model.Video;
import API.Model.VideoSeries;
import API.Util.Repositories.SeriesRepository;
import API.Util.Repositories.VideoRepository;
import API.Util.Repositories.VideoSeriesRepository;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Component
public class VideoBL {

  private VideoRepository videoRepository;
  private VideoSeriesRepository videoSeriesRepository;
  private SeriesRepository seriesRepository;
  private List<VideoSeries> videoSeriesList;
  private HashMap<String, Series> seriesMap;

  public VideoBL(
      VideoRepository videoRepository
      , VideoSeriesRepository videoSeriesRepository
      , SeriesRepository seriesRepository) throws SQLException {
    this.videoRepository = videoRepository;
    this.videoSeriesRepository = videoSeriesRepository;
    this.seriesRepository = seriesRepository;
  }

  public void transerVideoKeywords(Video video) throws SQLException {
    List<Tag> corresppondingTags = this.videoRepository.getTagsForVideo(video.id);
    String[] videoKeywords = video.videoKeyWords.split(",");
    Tag tag = new Tag(0, "", video.id, 1);
    for (String keyword : videoKeywords) {
      tag.value = keyword;
      if (!corresppondingTags.contains(tag)) {
        this.videoRepository.insertTag(tag);
      }
    }
  }

  public void transferCommentsToActualVideoId() throws SQLException {
    List<Video> videos = this.videoRepository.getAllPublishedVideos();
    for (Video video: videos) {
      boolean worked = this.videoRepository.transferCommentIds(video.videoID, video.id);

    }
  }

  public boolean addVideoToSeries(int videoId, int seriesId) throws SQLException {
      VideoSeries videoSeries = new VideoSeries(videoId, seriesId);
      boolean successful= this.videoSeriesRepository.addVideoToSeries(videoSeries);
      return successful;
  }


}
