package API.BusinessLayer.Storage;

import API.Model.Video;
import API.Util.Repositories.VideoRepository;
import API.Util.Repositories.VideoSeriesRepository;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class FeedHandler {

  private static SimpleDateFormat FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

  StorageHandler storageHandler;
  VideoRepository videoRepository;
  VideoSeriesRepository videoSeriesRepository;

  static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z");

  public FeedHandler(StorageHandler storageHandler, VideoRepository videoRepository, VideoSeriesRepository videoSeriesRepository) {
    this.storageHandler = storageHandler;
    this.videoRepository = videoRepository;
    this.videoSeriesRepository = videoSeriesRepository;
  }

  public void updateRSSFeed() {
    try {
      StringBuilder feed = new StringBuilder();
      feed.append("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n");
      feed.append("<channel>\n" +
          "        <atom:link href=\"https://doormonster.tv/feed\" rel=\"self\" type=\"application/rss+xml\" />\n" +
          "        <title>Door monster RSS feed</title>\n" +
          "        <link>https://doormonster.tv</link>\n" +
          "        <description>Your go to rss feed for all things doormonster</description>\n");
      List<Video> videos = this.videoRepository.getAllPublishedVideos();

      for (Video video : videos) {
        List<String> series = this.videoSeriesRepository.getVideoSeriesNames(video);
        if (series.size() != 0) {
          String seriesName = series.get(0);
          feed.append(
              String.format("<item>\n" +
                      "<title>%s</title>\n" +
                      "<link>https://doormonster.tv/video/%s</link>\n" +
                      "<description>\n" +
                      "<![CDATA[\n" +
                      "<p>\n" +
                      "<img src=\"%s/assets/images/videos/%s\" alt=\"\" />\n" +
                      "</p>]]>\n" +
                      "Door monster just uploaded a new video in the series %s called %s \n" +
                      "</description>\n" +
                      "<pubDate>%s</pubDate>\n" +
                      "<guid>https://doormonster.tv/video/%s</guid>" +
                      "</item>",
                  XMLSanitize(String.format("%s - %s", video.videoTitle, seriesName)),
                  video.videoID,
                  StorageHandler.BASE_AWS_URL,
                  sanitizeFilePath(video.videoThumbnail),
                  XMLSanitize(seriesName),
                  XMLSanitize(video.videoTitle),
                  formatDate(video.videoPublishDate),
                  video.videoID));
        }
      }
      feed.append("</channel>");
      feed.append("</rss>");

      this.storageHandler.storeRssFeed(feed.toString());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static String formatDate(LocalDateTime date) {
    return ZonedDateTime.ofInstant(date, ZoneOffset.UTC, ZoneId.systemDefault()).format(dateTimeFormatter);
  }

  private static String XMLSanitize(String unsanitaryString) {
    String sanitizedString = String.format("<![CDATA[%s]]>", unsanitaryString);
    return sanitizeFilePath(sanitizedString);
  }

  private static String sanitizeFilePath(String unsanitaryFilePath) {
    String sanitizedString = unsanitaryFilePath;
    sanitizedString = sanitizedString.replace("&amp;", "&");
    sanitizedString = sanitizedString.replace("rsquo;", "'");
    sanitizedString = sanitizedString.replace("#8216;", "'");
    sanitizedString = sanitizedString.replace("&#39;", "'");
    return sanitizedString;
  }
}
