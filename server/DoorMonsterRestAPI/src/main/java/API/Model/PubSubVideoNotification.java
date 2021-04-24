package API.Model;

import java.util.Date;

public class PubSubVideoNotification {
    public String title;
    public String description;
    public String videoLink;
    public String thumbnailLink;

    public PubSubVideoNotification() {
    }

    public PubSubVideoNotification(String title, String description, String videoLink, String thumbnailLink) {
        this.title = title;
        this.description = description;
        this.videoLink = videoLink;
        this.thumbnailLink = thumbnailLink;
    }
}
