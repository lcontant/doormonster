package API.Model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Blog {
    String blogId;
    String blogTitle;


    String blogThumbnail;
    String blogCategory;
    Date blogPublishDate;
    String blogAuthor;
    String blogContent;
    String blogKeyWords;
       public Blog(String blogId, String blogTitle, String blogThumbnail, String blogCategory, Date blogPublishDate, String blogAuthor, String blogContent, String blogKeyWords) {
        this.blogId = blogId;
        this.blogTitle = blogTitle;
        this.blogThumbnail = blogThumbnail;
        this.blogCategory = blogCategory;
        this.blogPublishDate = blogPublishDate;
        this.blogAuthor = blogAuthor;
        this.blogContent = blogContent;
        this.blogKeyWords = blogKeyWords;
    }
    public Blog(ResultSet rs) throws SQLException {
        this(
                rs.getString("blogId")
                ,rs.getString("blogTitle")
                ,rs.getString("blogThumbnail")
                ,rs.getString("blogCategory")
                ,Date.valueOf(LocalDate.parse(rs.getString("blogPublishDate"),DateTimeFormatter.ISO_DATE))
                ,rs.getString("blogAuthor")
                ,rs.getString("blogContent")
                ,rs.getString("blogKeyWords")
        );
    }
}
