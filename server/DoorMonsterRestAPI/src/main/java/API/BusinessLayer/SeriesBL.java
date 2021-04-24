package API.BusinessLayer;

import API.Model.Series;
import API.Model.Video;
import API.Model.VideoSeries;
import API.Ressource.SeriesWithVideos;
import API.Util.Repositories.SeriesRepository;
import API.Util.Repositories.VideoRepository;
import API.Util.Repositories.VideoSeriesRepository;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class SeriesBL {

  private SeriesRepository seriesRepository;
  private VideoSeriesRepository videoSeriesRepository;
  private VideoRepository videoRepository;

  public SeriesBL(SeriesRepository seriesRepository, VideoSeriesRepository videoSeriesRepository, VideoRepository videoRepository) {
    this.seriesRepository = seriesRepository;
    this.videoSeriesRepository = videoSeriesRepository;
    this.videoRepository = videoRepository;
  }

}
