package API.Ressource;

import API.Model.Series;
import API.Model.Video;

import java.util.ArrayList;
import java.util.List;

public class SeriesWithVideos {
    public Series series;
    public List<Video> videos;


    public SeriesWithVideos() {
    }

    public SeriesWithVideos(Series series) {
        this.series = series;
        this.videos = new ArrayList<>();
    }

    public SeriesWithVideos(Series series, List<Video> videos) {
        this.series = series;
        this.videos = videos;
    }
}
