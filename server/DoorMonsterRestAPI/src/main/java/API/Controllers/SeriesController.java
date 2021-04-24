package API.Controllers;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import API.BusinessLayer.AuthenticationHandler;
import API.BusinessLayer.LogBusinessLayer;
import API.BusinessLayer.Storage.StorageHandler;
import API.Model.Series;
import API.Model.UserDto;
import API.Util.JSONMapper;
import API.Util.Repositories.SeriesRepository;
import API.Util.Repositories.UserRepository;
import API.Util.Repositories.VideoRepository;
import API.Util.Repositories.VideoSeriesRepository;
import com.mysql.cj.protocol.x.XMessage;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import javax.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("series")
public class SeriesController {

  private SeriesRepository seriesRepository;
  private VideoSeriesRepository videoSeriesRepository;
  private VideoRepository videoRepository;
  private UserRepository userRepository;
  private LogBusinessLayer logBusinessLayer;
  private AuthenticationHandler authenticationHandler;
  private StorageHandler storageHandler;

  public SeriesController(SeriesRepository seriesRepository
      , VideoSeriesRepository videoSeriesRepository
      , VideoRepository videoRepository
      , UserRepository userRepository
      , LogBusinessLayer logBusinessLayer
      , AuthenticationHandler authenticationHandler
      , StorageHandler storageHandler) {
    this.seriesRepository = seriesRepository;
    this.videoSeriesRepository = videoSeriesRepository;
    this.videoRepository = videoRepository;
    this.userRepository = userRepository;
    this.logBusinessLayer = logBusinessLayer;
    this.authenticationHandler = authenticationHandler;
    this.storageHandler = storageHandler;
  }

  @GetMapping("/all/ordered")
  public ResponseEntity<String> getAllSeriesInOrderOfUpdate() {
    String response = JSONMapper.getInstance().JSONStringify("Couldn't get the series");
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    try {
      List<Series> orderedSeries = this.seriesRepository.getSeriesInOrderOfUpdate();
      response = JSONMapper.getInstance().JSONStringify(orderedSeries);
      status = HttpStatus.OK;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(response, status);
  }

  @GetMapping("/all")
  public ResponseEntity<String> getAllSeries() {
    String response = JSONMapper.getInstance().JSONStringify("No");
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    try {
      List<Series> allSeries = this.seriesRepository.getAllSeries();
      response = JSONMapper.getInstance().JSONStringify(allSeries);
      status = HttpStatus.OK;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(response, status);
  }

  @GetMapping("/name/{id}")
  public ResponseEntity<String> getSeriesNameForVideo(@PathVariable("id") int id) {
    String response = JSONMapper.getInstance().JSONStringify("Couldn't find the series name");
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    try {
      List<Series> correspondingSeries = this.videoSeriesRepository.getSeriesForVideo(id);
      if (correspondingSeries.size() > 0) {
        response = JSONMapper.getInstance().JSONStringify(correspondingSeries.get(0));
        status = HttpStatus.OK;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(response, status);
  }

  @GetMapping("/names")
  public ResponseEntity<String> getAllSeriesName() {
    String response = JSONMapper.getInstance().JSONStringify("");
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    List<String> seriesNames = null;
    try {
      seriesNames = this.videoRepository.getVideoSeriesName();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    if (seriesNames.size() > 0) {
      response = JSONMapper.getInstance().JSONStringify(seriesNames);
      status = HttpStatus.OK;
    }
    return new ResponseEntity<>(response, status);
  }

  @PutMapping("/update")
  public ResponseEntity<String> updateSeries(@RequestBody Series series, @RequestHeader("SessionId") String sessionId) {
    String message = JSONMapper.getInstance().JSONStringify("Couldn't update the playlist");
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    try {
      UserDto user = this.userRepository.getBySessionId(sessionId);
      if (user != null) {
        if (this.seriesRepository.UpdateSeries(series)) {
          message = JSONMapper.getInstance().JSONStringify("Playlist updated");
          status = HttpStatus.OK;
        }
      } else {
        message = JSONMapper.getInstance().JSONStringify("Bad sessionId");
        status = HttpStatus.BAD_REQUEST;
      }
    } catch (SQLException e) {
      this.logBusinessLayer.LogError(e);
    }
    return new ResponseEntity<>(message, status);
  }
  @RequestMapping(path = "/{id}", method = GET)
  public String getSeriesById(@PathVariable String id) {
    Series series = null;
    try {
      series = seriesRepository.getSeriesByTextId(id);
    } catch (SQLException | ParseException e) {
      e.printStackTrace();
    }
    return JSONMapper.getInstance().JSONStringify(series);
  }
  @RequestMapping(path = "/")
  public String getSeries() throws SQLException  {
    List<Series> series = seriesRepository.getAllSeries();
    return JSONMapper.getInstance().JSONStringify(series);
  }

  @PostMapping("/upload")
  public ResponseEntity<String> uploadNewSeries(@RequestBody Series series, @RequestHeader("SessionId") String sessionId) {
    String message = "There was an error uploading the series";
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    try {
      if (this.authenticationHandler.userIsAdmin(sessionId)) {
        UserDto user = this.userRepository.getBySessionId(sessionId);
        series.creatorId = user.userId;
        boolean seriesCreated = this.seriesRepository.CreateNewSeries(series);
        if (seriesCreated) {
          message = "Series data uploaded";
          status = HttpStatus.OK;
        }
      } else {
        message = "Unauthorized request";
        status = HttpStatus.UNAUTHORIZED;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify(message), status);
  }

  @PostMapping("/thumbnail/{seriesId}")
  public ResponseEntity<String> uploadNewThumbnail(@RequestParam("file") MultipartFile file, @RequestHeader("SessionId") String sessionId, @PathVariable("seriesId") String seriesId) {
    String message = "An error occured while uploading the file";
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    try {
      if (this.authenticationHandler.userIsAdmin(sessionId)) {
        this.storageHandler.storeSeriesThumbnailFile(file,seriesId);
        message = "thumbnail uploaded";
        status = HttpStatus.OK;
      } else {
        message = "Unauthorized request";
        status = HttpStatus.UNAUTHORIZED;
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify(message), status);
  }
}
