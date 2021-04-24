package API.Controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class VideoControllerTest {

    @Autowired
    VideoController videoController;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getAllVideosShouldErrorWithoutSessionId() {
        ResponseEntity<String> response =videoController.getAllVideos(null);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getVideos() {
    }

    @Test
    public void searchVideo() {
    }

    @Test
    public void getLatest() {
    }

    @Test
    public void getLatestVideos() {
    }

    @Test
    public void getEpisodesFor() {
    }

    @Test
    public void getAllEpisodesForAllSeries() {
    }

    @Test
    public void getSeries() {
    }

    @Test
    public void getSeriesById() {
    }

    @Test
    public void getVideoById() {
    }

    @Test
    public void createSeries() {
    }

    @Test
    public void addViewToVide() {
    }

    @Test
    public void uploadVideo() {
    }

    @Test
    public void uploadThumbnail() {
    }

    @Test
    public void getAllSeriesName() {
    }

    @Test
    public void getAllCategoriesNames() {
    }

    @Test
    public void getAllVideos() {
    }

    @Test
    public void updateVideo() {
    }

    @Test
    public void deleteVideo() {
    }
}