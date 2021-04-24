package API.Controllers;

import API.BusinessLayer.AuthenticationHandler;
import API.BusinessLayer.Storage.StorageHandler;
import API.Model.PodCastEpisode;
import API.Model.Role;
import API.Model.UserDto;
import API.Util.JSONMapper;
import API.Util.Repositories.PodcastEpisodeRepository;

import API.Util.Repositories.RoleRepository;
import API.Util.Repositories.UserRepository;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/podcastepisodes")
public class PodcastEpisodesController {

  PodcastEpisodeRepository podcastEpisodeRepository;
  UserRepository userRepository;
  RoleRepository roleRepository;
  StorageHandler storageHandler;
  AuthenticationHandler authenticationHandler;

  public PodcastEpisodesController(PodcastEpisodeRepository podcastEpisodeRepository, UserRepository userRepository, RoleRepository roleRepository, StorageHandler storageHandler, AuthenticationHandler authenticationHandler) {
    this.podcastEpisodeRepository = podcastEpisodeRepository;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.storageHandler = storageHandler;
    this.authenticationHandler = authenticationHandler;
  }

  @RequestMapping(method = GET)
  public String getAll() {
    List<PodCastEpisode> podcastEpisodes = null;
    podcastEpisodes = this.podcastEpisodeRepository.getPublishedList();
    return JSONMapper.getInstance().JSONStringify(podcastEpisodes);
  }

  @RequestMapping(path = "/{id}", method = GET)
  public String getById(@PathVariable String id) {
    PodCastEpisode podCastEpisode = null;
    try {
      podCastEpisode = this.podcastEpisodeRepository.getById(id);
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return JSONMapper.getInstance().JSONStringify(podCastEpisode);
  }

  @PostMapping(path = "/upload")
  public ResponseEntity<String> uploadpodcastEpisode(@RequestHeader("SessionId") String sessionId, @RequestBody PodCastEpisode episode) {
    UserDto user;
    Role role;
    String response = JSONObject.valueToString("Couldn't upload the podcast");
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    try {
      user = this.userRepository.getBySessionId(sessionId);
      if (user != null) {
        role = this.roleRepository.getRoleByUserId(user.userId);
        if (role != null && role.ranking == 0) {
          if (this.podcastEpisodeRepository.insertPodcast(episode)) {
            List<PodCastEpisode> episodes = this.podcastEpisodeRepository.getEpisodesFor(episode.podcast);
            response = JSONMapper.getInstance().JSONStringify(episodes.get(Math.max(episodes.size() - 1, 0)));
            status = HttpStatus.OK;
          }
        } else {
          response = JSONObject.valueToString("Invalid user");
          status = HttpStatus.BAD_REQUEST;
        }
      } else {
        response = JSONObject.valueToString("Invalid user");
        status = HttpStatus.BAD_REQUEST;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(response, status);
  }

  @PostMapping(path = "/upload/audio/{id}")
  public ResponseEntity<String> uploadEpisodeAudio(@PathVariable("id") int episodeId, @RequestParam("file") MultipartFile multipartFile, @RequestHeader("SessionId") String sessionId) {
    UserDto user;
    Role role;
    PodCastEpisode podCastEpisode;
    String response = JSONObject.valueToString("Couldn't upload the thumbnail");
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    try {
      if (this.authenticationHandler.userIsAdmin(sessionId)) {
        podCastEpisode = this.podcastEpisodeRepository.getById(String.valueOf(episodeId));
        if (podCastEpisode != null) {
          podCastEpisode.episodeLink = StorageHandler.BASE_AWS_URL + "/" + this.storageHandler.storePodcastAudioFile(multipartFile);
          if (this.podcastEpisodeRepository.updatePodcastEpisode(podCastEpisode)) {
            response = JSONMapper.getInstance().JSONStringify(podCastEpisode);
            status = HttpStatus.OK;
          }
        } else {
          response = JSONObject.valueToString("couldn't find the user");
          status = HttpStatus.BAD_REQUEST;
        }
      } else {
        response = JSONObject.valueToString("couldn't find the user");
        status = HttpStatus.BAD_REQUEST;
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(response, status);
  }

  @PostMapping(path = "/upload/thumbnail/{id}")
  public ResponseEntity<String> uploadEpisodeThumbnail(@PathVariable("id") int episodeId, @RequestParam("file") MultipartFile multipartFile, @RequestHeader("SessionId") String sessionId) {
    PodCastEpisode podCastEpisode;
    String response = JSONObject.valueToString("Couldn't upload the thumbnail");
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    try {
      if (this.authenticationHandler.userIsAdmin(sessionId)) {
        podCastEpisode = this.podcastEpisodeRepository.getById(String.valueOf(episodeId));
        if (podCastEpisode != null) {
          podCastEpisode.episodeLink = StorageHandler.BASE_AWS_URL + this.storageHandler.storePodcastImageFile(multipartFile);
          response = JSONObject.valueToString(podCastEpisode);
          status = HttpStatus.OK;
        } else {
          response = JSONObject.valueToString("couldn't find the user");
          status = HttpStatus.BAD_REQUEST;
        }
      } else {
        response = JSONObject.valueToString("couldn't find the user");
        status = HttpStatus.BAD_REQUEST;
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(response, status);
  }

  public String getEpisodeCount(String title) {
    Integer count = null;
    try {
      count = this.podcastEpisodeRepository.getEpisodeCount(title);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return JSONMapper.getInstance().JSONStringify(count);
  }

  @RequestMapping(path = "/episodes/{title}", method = GET)
  public String getEpisodes(@PathVariable String title) {
    List<PodCastEpisode> episodes = null;
    episodes = this.podcastEpisodeRepository.getEpisodesFor(title);
    return JSONMapper.getInstance().JSONStringify(episodes);

  }
}
