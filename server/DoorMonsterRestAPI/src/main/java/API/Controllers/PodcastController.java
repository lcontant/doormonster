package API.Controllers;


import API.BusinessLayer.AuthenticationHandler;
import API.BusinessLayer.Storage.StorageHandler;
import API.Model.*;
import API.Util.JSONMapper;
import API.Util.Repositories.PodcastRepository;
import API.Util.Repositories.RoleRepository;
import API.Util.Repositories.SupporterRepository;
import API.Util.Repositories.UserRepository;
import java.io.IOException;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.jooq.JSON;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/podcasts")
public class PodcastController {

  PodcastRepository podcastRepository;
  UserRepository userRepository;
  SupporterRepository supporterRepository;
  RoleRepository roleRepository;
  AuthenticationHandler authenticationHandler;
  StorageHandler storageHandler;

  public PodcastController(PodcastRepository podcastRepository, UserRepository userRepository, SupporterRepository supporterRepository, RoleRepository roleRepository, AuthenticationHandler authenticationHandler, StorageHandler storageHandler) {
    this.podcastRepository = podcastRepository;
    this.userRepository = userRepository;
    this.supporterRepository = supporterRepository;
    this.roleRepository = roleRepository;
    this.authenticationHandler = authenticationHandler;
    this.storageHandler = storageHandler;
  }

  @RequestMapping(path = "", method = GET)
  public String getAll() {
    List<Podcast> podcasts = null;
      podcasts = this.podcastRepository.getAllSeriesWithPublishedEpisodes();
    return JSONMapper.getInstance().JSONStringify(podcasts);
  }

  @GetMapping("/all")
  public ResponseEntity<String> getAbsoluteAll(@RequestHeader("SessionId") String sessionId) {
    String message = JSONMapper.getInstance().JSONStringify(MESSAGES_CONSTANTS.UNEXPECTED_ERROR_ENCOUNTERED);
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    try {
      if (this.authenticationHandler.userIsAdmin(sessionId)) {
        message = JSONMapper.getInstance().JSONStringify(this.podcastRepository.getAllSeries());
        status = HttpStatus.OK;
      } else {
        message = JSONMapper.getInstance().JSONStringify(MESSAGES_CONSTANTS.UNEXPECTED_ERROR_ENCOUNTERED);
        status = HttpStatus.UNAUTHORIZED;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(message, status);
  }

  @GetMapping("/{title}")
  public ResponseEntity<String> getPodcastByTitle(@PathVariable("title") String title) throws SQLException {
    String responseMesasage = JSONMapper.getInstance().JSONStringify("Couldn't get the podcast");
    HttpStatus status = HttpStatus.BAD_REQUEST;
    Podcast podcast = this.podcastRepository.getByTitle(title);
    if (podcast != null) {
      responseMesasage = JSONMapper.getInstance().JSONStringify(podcast);
      status = HttpStatus.OK;
    }
    return new ResponseEntity<>(responseMesasage, status);
  }

  @GetMapping("/series")
  public ResponseEntity<String> getAllPodcastSeries() {
    HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    String responseMessage = JSONMapper.getInstance().JSONStringify(MESSAGES_CONSTANTS.UNEXPECTED_ERROR_ENCOUNTERED);
      List<Podcast> podcasts = this.podcastRepository.getAllSeriesWithPublishedEpisodes();
      responseMessage = JSONMapper.getInstance().JSONStringify(podcasts);
      responseStatus = HttpStatus.OK;
    return new ResponseEntity<>(responseMessage, responseStatus);
  }

  @GetMapping("/supporter")
  public ResponseEntity<String> getAllpodcastsInludingSupporterOnly(@RequestHeader("SessionId") String sessionId) {
    HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    String responseMessage = JSONMapper.getInstance().JSONStringify(MESSAGES_CONSTANTS.UNEXPECTED_ERROR_ENCOUNTERED);
    UserDto user = null;
    Supporter supporter = null;
    Role role = null;
    try {
      user = this.userRepository.getBySessionId(sessionId);
      if (user != null) {
        role = this.roleRepository.getRoleByUserId(user.userId);
        supporter = this.supporterRepository.getSupporterByUserId(user.userId);
        if (role != null && role.ranking == 0 || supporter != null && supporter.ammount >= 500 || user.patreonContribution >= 500) {
          List<Podcast> podcasts = this.podcastRepository.getAllSeriesIncludingSupporterOnly();
          responseMessage = JSONMapper.getInstance().JSONStringify(podcasts);
          responseStatus = HttpStatus.OK;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(responseMessage, responseStatus);
  }

  @PostMapping("/upload")
  public ResponseEntity<String> uploadNewSeries(@RequestHeader("SessionId") String sessionId, @RequestBody Podcast podcast) {
    HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    String responseMessage = JSONMapper.getInstance().JSONStringify(MESSAGES_CONSTANTS.UNEXPECTED_ERROR_ENCOUNTERED);
    UserDto user = null;
    Supporter supporter = null;
    Role role = null;
    try {
      if (this.authenticationHandler.userIsAdmin(sessionId)) {
        boolean operationSuccessfull = this.podcastRepository.createNewPodcast(podcast);
        if (operationSuccessfull) {
          responseStatus = HttpStatus.OK;
          responseMessage = JSONMapper.getInstance().JSONStringify("Podcast created");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(responseMessage, responseStatus);
  }

  @PostMapping("/uploadThumbnail")
  public ResponseEntity<String> uploadPocastSeriesThumbnail(@RequestParam("file") MultipartFile file, @RequestHeader("SessionId") String sessionId) {
    String responseMessage = JSONMapper.getInstance().JSONStringify(MESSAGES_CONSTANTS.UNEXPECTED_ERROR_ENCOUNTERED);
    HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    try {
      if (this.authenticationHandler.userIsAdmin(sessionId)) {
        this.storageHandler.storePodcastSeriesThumbnail(file);
        responseMessage = JSONMapper.getInstance().JSONStringify("Podcast series thumbnail upload successfull");
        responseStatus = HttpStatus.OK;
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(responseMessage, responseStatus);
  }


}
