package API.Controllers;


import API.BusinessLayer.LogBusinessLayer;
import API.BusinessLayer.PubSubHandler;
import API.BusinessLayer.Storage.StorageHandler;
import API.BusinessLayer.VideoBL;
import API.BusinessLayer.VideoPublisher;
import API.Model.Role;
import API.Model.Series;
import API.Model.UserDto;
import API.Model.Video;
import API.Ressource.SeriesWithVideos;
import API.Util.JSONMapper;
import API.Util.Repositories.*;
import API.databases.tables.records.SeriesRecord;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import software.amazon.awssdk.http.HttpStatusCode;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("video")
public class VideoController {

  VideoRepository videoRepository;
  SeriesRepository seriesRepository;
  StorageHandler storageHandler;
  UserRepository userRepository;
  RoleRepository roleRepository;
  PubSubHandler pubSubHandler;
  VideoPublisher videoPublisher;
  LogBusinessLayer logBusinessLayer;
  VideoBL videoBL;
  VideoSeriesRepository videoSeriesRepository;

  public VideoController(VideoRepository videoRepository
      , SeriesRepository seriesRepository
      , StorageHandler storageHandler
      , UserRepository userRepository
      , RoleRepository roleRepository
      , PubSubHandler pubSubHandler
      , VideoPublisher videoPublisher
      , LogBusinessLayer logRepository
      , VideoBL videoBL
      , VideoSeriesRepository videoSeriesRepository) {
    this.videoRepository = videoRepository;
    this.seriesRepository = seriesRepository;
    this.userRepository = userRepository;
    this.storageHandler = storageHandler;
    this.roleRepository = roleRepository;
    this.pubSubHandler = pubSubHandler;
    this.videoPublisher = videoPublisher;
    this.logBusinessLayer = logRepository;
    this.videoBL = videoBL;
    this.videoSeriesRepository = videoSeriesRepository;
  }

  @RequestMapping(method = GET)
  public String getVideos() throws SQLException {

    List<Video> videos = this.videoRepository.getAllPublishedVideos();

    return JSONMapper.getInstance().JSONStringify(videos);
  }

  @RequestMapping("/search/{query}")
  public String searchVideo(@PathVariable String query) {
    List<Video> videos = null;
    try {
      videos = this.videoRepository.search(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return JSONMapper.getInstance().JSONStringify(videos);
  }

  @RequestMapping(path = "/featured", method = GET)
  public String getLatest() {
    Video video = null;
    try {
      video = this.videoRepository.getFeatured();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return JSONMapper.getInstance().JSONStringify(video);
  }


  @RequestMapping(path = "/latest/{videoNumber}", method = GET)
  public String getLatestVideos(@PathVariable int videoNumber) {
    List<Video> videos = null;
    try {
      videos = this.videoRepository.getLatestVideos(videoNumber);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return JSONMapper.getInstance().JSONStringify(videos);
  }

  @RequestMapping(path = "/episodes/{title}", method = GET)
  public String getEpisodesFor(@PathVariable String title) {
    List<Video> videos = null;
    String response;
    try {
      videos = this.videoRepository.getVideosForSeries(title);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    response = JSONMapper.getInstance().JSONStringify(videos);
    return response;
  }

  @RequestMapping(path = "/series/episodes", method = GET)
  public ResponseEntity<String> getAllEpisodesForAllSeries(@RequestHeader int numberOfEpisodes) {
    String responseContent = "";
    HttpStatus status = HttpStatus.OK;
    List<SeriesWithVideos> seriesWithVideos;
    try {
      seriesWithVideos = this.videoSeriesRepository.getSeriesWithVideos(numberOfEpisodes);
      responseContent = JSONMapper.getInstance().JSONStringify(seriesWithVideos);
    } catch (SQLException e) {
      e.printStackTrace();
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return new ResponseEntity<String>(responseContent, status);
  }


  @RequestMapping(path = "/series")
  public String getSeries() throws SQLException, ParseException {
    List<Series> series = seriesRepository.getAllSeries();
    return JSONMapper.getInstance().JSONStringify(series);
  }


  @RequestMapping(path = "/{id}", method = GET)
  public String getVideoById(@PathVariable String id) {
    Video video = null;
    try {
      video = this.videoRepository.getById(id);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return JSONMapper.getInstance().JSONStringify(video);

  }

  @PostMapping("/series/create")
  public ResponseEntity<String> createSeries(@RequestBody Series series, @RequestHeader("SessionId") String sessionId) {
    String message = JSONMapper.getInstance().JSONStringify("Couldn't create the series");
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    boolean insertSuccesfull = false;
    UserDto user = null;
    try {
      user = this.userRepository.getBySessionId(sessionId);
      if (user != null && user.userId == series.creatorId) {
        insertSuccesfull = this.seriesRepository.CreateNewSeries(series);
        if (insertSuccesfull) {
          message = JSONMapper.getInstance().JSONStringify("Series created");
          status = HttpStatus.OK;
        }
      }
    } catch (SQLException e) {

    }
    return new ResponseEntity<>(message, status);
  }



  @PutMapping("/add/view/{id}")
  @Caching(evict = {
      @CacheEvict(value = "episodes", allEntries = true),
      @CacheEvict(value = "Allepisodes", allEntries = true)
  })
  public ResponseEntity<String> addViewToVide(@PathVariable String id) {
    String response = JSONMapper.getInstance().JSONStringify("Error while adding view to video");
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    boolean succesful = false;
    try {
      succesful = this.videoRepository.addViewToVideo(id);
      if (!succesful) {
        response = "There was an error updating the views";
        status = HttpStatus.INTERNAL_SERVER_ERROR;
      } else {
        response = "Views updated succesfully";
        status = HttpStatus.OK;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<String>(JSONMapper.getInstance().JSONStringify(response), status);

  }

  @PostMapping("/upload")
  public ResponseEntity<String> uploadVideo(@RequestBody Video video, @RequestHeader("SessionId") String sessionId, @RequestHeader("seriesId") int[] seriesId) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
    UserDto uploader;
    Role uploaderRole;
    String response;
    HttpStatus status;
    boolean succesful;
    response = "";
    status = HttpStatus.INTERNAL_SERVER_ERROR;
    try {
      uploader = this.userRepository.getBySessionId(sessionId);
      uploaderRole = this.roleRepository.getRoleByUserId(uploader.userId);
      if (uploaderRole != null && uploaderRole.ranking == 0) {
        succesful = this.videoRepository.uploadVideo(video);
        if (succesful) {
          Video uploadedVideo = this.videoRepository.getVideoByTitle(video.videoTitle);
          this.videoBL.transerVideoKeywords(uploadedVideo);
          for (int seriesIdIt : seriesId) {
            this.videoBL.addVideoToSeries(uploadedVideo.id, seriesIdIt);
          }
          response = JSONMapper.getInstance().JSONStringify(true);
          status = HttpStatus.OK;
          this.pubSubHandler.sendVideoToPubSubListeners(video);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(response, status);
  }

  @PostMapping(path = "/thumbnail")
  public ResponseEntity<String> uploadThumbnail(@RequestParam("file") MultipartFile file, @RequestHeader(name = "prePath", required = false) String prePath, @RequestHeader("SessionId") String sessionId) {
    String response = "There was a problem uploading the thumbnail";
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    UserDto uploader;
    Role uploaderRole;
    try {
      uploader = this.userRepository.getBySessionId(sessionId);
      if (uploader != null) {
        uploaderRole = this.roleRepository.getRoleByUserId(uploader.userId);
        if (uploaderRole != null && uploaderRole.ranking == 0) {
          this.storageHandler.storeVideoThumbnail(file, prePath);
          this.videoPublisher.checkForVideoUploads();
          response = JSONMapper.getInstance().JSONStringify("file uploaded");
          status = HttpStatus.OK;

        }
      }
    } catch (IOException | SQLException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(response, status);
  }


  @GetMapping("/categories/name")
  public ResponseEntity<String> getAllCategoriesNames() {
    String response = JSONMapper.getInstance().JSONStringify("");
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    List<String> categoriesNames = null;
    try {
      categoriesNames = this.videoRepository.getVideoCategoriesNames();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    if (categoriesNames.size() > 0) {
      response = JSONMapper.getInstance().JSONStringify(categoriesNames);
      status = HttpStatus.OK;
    }
    return new ResponseEntity<>(response, status);
  }

  @GetMapping("/all")
  public ResponseEntity<String> getAllVideos(@RequestHeader("SessionId") String sessionId) {
    String response = JSONMapper.getInstance().JSONStringify("I'm afraid I can't do that, dave");
    HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
    UserDto demander;
    Role demanderRole;
    List<Video> videos;
    try {
      demander = this.userRepository.getBySessionId(sessionId);
      if (demander != null) {
        demanderRole = this.roleRepository.getRoleByUserId(demander.userId);
        if (demanderRole.ranking == 0) {
          videos = this.videoRepository.getAllVideos();
          response = JSONMapper.getInstance().JSONStringify(videos);
          responseStatus = HttpStatus.OK;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(response, responseStatus);
  }

  @PutMapping("/update")
  public ResponseEntity<String> updateVideo(@RequestBody Video video, @RequestHeader("SessionId") String sessionId) {
    String response = JSONMapper.getInstance().JSONStringify("Couldn't update");
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    UserDto editor;
    Role editorRole;
    boolean editSuccesful = false;
    try {
      editor = this.userRepository.getBySessionId(sessionId);
      if (editor != null) {
        editorRole = this.roleRepository.getRoleByUserId(editor.userId);
        if (editorRole.ranking == 0) {
          editSuccesful = this.videoRepository.updateVideo(video);
          if (editSuccesful) {
            response = JSONMapper.getInstance().JSONStringify("Video updated");
            status = HttpStatus.OK;
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(response, status);
  }

  @DeleteMapping("/delete/{videoId}")
  public ResponseEntity<String> deleteVideo(@PathVariable("videoId") int id, @RequestHeader("SessionId") String sessionId) {
    String response = JSONMapper.getInstance().JSONStringify("Couldn't delete the video");
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    UserDto deleter;
    Role deleterRole;
    boolean deleted = false;
    try {
      deleter = this.userRepository.getBySessionId(sessionId);
      if (deleter != null) {
        deleterRole = this.roleRepository.getRoleByUserId(deleter.userId);
        if (deleterRole.ranking == 0) {
          deleted = this.videoRepository.deleteVideo(id);
          if (deleted) {
            response = JSONMapper.getInstance().JSONStringify("Deleted the video");
            status = HttpStatus.OK;
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(response, status);
  }

  @PostMapping("/upload/file")
  public ResponseEntity<String> uploadVideoFileForVideo(@RequestParam("file") MultipartFile file
      , @RequestHeader("SessionId") String sessionId) {
    String response = JSONMapper.getInstance().JSONStringify("Couldn't upload the file tell Louis");
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    UserDto uploader;
    Role uploaderRole;
    try {
      uploader = this.userRepository.getBySessionId(sessionId);
      if (uploader != null) {
        uploaderRole = this.roleRepository.getRoleByUserId(uploader.userId);
        if (uploaderRole != null && uploaderRole.ranking == 0) {
              String awsResponse = this.storageHandler.storeVideoFile(file);
              response = JSONMapper.getInstance().JSONStringify("Video file uploaded");
              status = HttpStatus.OK;
          }
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(response, status);
  }






}
