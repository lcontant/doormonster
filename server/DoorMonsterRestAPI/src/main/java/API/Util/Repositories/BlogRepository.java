package API.Util.Repositories;

import API.Model.Blog;
import API.Util.SQLConnector.ConnectionManager;
import org.springframework.stereotype.Component;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class BlogRepository {

    ConnectionManager connectionManager;

    public BlogRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public synchronized List<Blog> GetList() throws SQLException {

        List<Blog> blogs = new ArrayList<>();
        String request = "SELECT  * FROM blog ORDER BY blogPublishDate DESC";
        ResultSet rs = this.connectionManager.query(request);
        while (rs.next()) {
            blogs.add(new Blog(rs));
        }

        return blogs;
    }

    public synchronized Blog getById(String id) throws SQLException {
        String request = "Select * from blog where blogID = " + id;
        ResultSet rs = this.connectionManager.query(request);
        Blog blog = null;
        if (rs.next()){
            blog = new Blog(rs);
        }
        return blog;
    }
}
