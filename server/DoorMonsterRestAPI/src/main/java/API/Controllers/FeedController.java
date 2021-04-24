package API.Controllers;

import API.BusinessLayer.Storage.FeedHandler;
import API.BusinessLayer.Storage.StorageHandler;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("Feed")
public class FeedController {


    FeedHandler feedHandler;
    StorageHandler storageHandler;


    public FeedController(FeedHandler feedHandler, StorageHandler storageHandler) {

        this.feedHandler = feedHandler;
        this.storageHandler = storageHandler;

    }

    @PutMapping("/update")
    public void updateFeed() {
        this.feedHandler.updateRSSFeed();
    }
}
